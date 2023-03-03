package io.github.afezeria.freedao.processor.core.method

import io.github.afezeria.freedao.Long2IntegerResultHandler
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.MappingData
import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.sqlQuote


abstract class CrudMethod private constructor(
    daoHandler: DaoHandler,
    method: LazyMethod,
) : AbstractMethodDefinition(daoHandler, method) {

    //    val crudEntity: EntityObjectModel
    val crudEntity: EntityType

    val parameterName: String

    init {
//        crudEntity = daoHandler.crudEntity
//            ?: throw HandlerException("Method $name requires Dao.crudEntity to be specified")

        crudEntity = EntityType(
            daoHandler.crudEntityType
                ?: throw HandlerException("Method $qualifiedName requires Dao.crudEntity to be specified")
        )

        val parameter = parameters.find { it.type.isAssignable(crudEntity) }
            ?: throw HandlerException("Missing parameter of type ${crudEntity.qualifiedName}")
        parameterName = parameter.simpleName
    }


    val where: String by lazy {
        crudEntity.allFields.joinToString(separator = "\n") {
            //language=Xml
            """
                    <if test='${parameterName}.${it.simpleName} != null'>
                    and ${it.columnName.sqlQuote()} = ${it.templateParameterStr(parameterName)}
                    </if>
            """.trimIndent()
        }
    }


    companion object {
        operator fun invoke(
            daoHandler: DaoHandler,
            method: LazyMethod,
        ): CrudMethod? {
            return when (method.simpleName) {
                "count" -> Count(daoHandler, method)
                "delete" -> Delete(daoHandler, method)
                "insert" -> Insert(daoHandler, method)
                "insertNonNullFields" -> InsertNonNullFields(daoHandler, method)
                "update" -> Update(daoHandler, method)
                "updateNonNullFields" -> UpdateNonNullFields(daoHandler, method)
                "list" -> ListMethod(daoHandler, method)
                else -> null
            }
        }
    }

    class Count(daoHandler: DaoHandler, method: LazyMethod) : CrudMethod(daoHandler, method) {
        init {
            returnTypeItemType.apply {
                if (!isSameType(Int::class)
                    && !isSameType(Long::class)
                ) {
                    throw HandlerException("The return type of count method must be Integer/int or Long/long")
                }
            }
            mappings += MappingData(
                source = "_cot",
                target = "",
                //count默认返回值为Long，如果当前函数返回类型为long/Long则不需要handler处理
                typeHandlerLA = Long2IntegerResultHandler::class.typeLA.takeIf {
                    returnTypeItemType.isSameType(Int::class)
                },
                targetTypeLA = Int::class.typeLA.takeIf {
                    returnTypeItemType.isSameType(Int::class)
                },
                constructorParameterIndex = -1
            )
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                <select>
                select count(*) as _cot from ${crudEntity.dbFullyQualifiedName}
                <if test='${parameterName} != null'>
                <where>
                $where
                </where>
                </if>
                </select>
            """.trimIndent()
        }
    }

    class Delete(daoHandler: DaoHandler, method: LazyMethod) : CrudMethod(daoHandler, method) {
        init {
            returnUpdateCount = true
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                <delete>
                delete from ${crudEntity.dbFullyQualifiedName}
                where
                <if test='$parameterName != null'>
                <trim suffixOverrides='' prefixOverrides='and '>
                $where
                </trim>
                </if>
                </delete>
            """.trimIndent()
        }
    }

    class ListMethod(daoHandler: DaoHandler, method: LazyMethod) : CrudMethod(daoHandler, method) {

        init {
            if (returnTypeContainerType == null || !returnTypeItemType.isSameType(crudEntity)) {
                throw HandlerException("The return type must be assignable to Collection<${crudEntity}>")
            }
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                <select>
                select ${mappings.joinToString { it.source }} from ${crudEntity.dbFullyQualifiedName}
                <if test='${parameterName} != null'>
                <where>
                $where
                </where>
                </if>
                </select>
            """.trimIndent()
        }
    }

    open class Insert(daoHandler: DaoHandler, method: LazyMethod) : CrudMethod(daoHandler, method) {
        val insertProperties: List<BeanProperty>

        init {
            returnUpdateCount = true
            insertProperties = crudEntity.allFields.filter { it.exist && it.supportInsert }
            if (insertProperties.isEmpty()) {
                throw HandlerException("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
            }
        }

        override fun getTemplate(): String {

            //language=xml
            return """
                <insert>
                insert into ${crudEntity.dbFullyQualifiedName} (${insertProperties.joinToString { it.columnName.sqlQuote() }})
                values (${insertProperties.joinToString { it.templateParameterStr(parameterName) }})
                </insert>
            """.trimIndent()
        }

    }

    class InsertNonNullFields(daoHandler: DaoHandler, method: LazyMethod) : Insert(daoHandler, method) {

        override fun getTemplate(): String {
            val columns = insertProperties.joinToString(
                separator = "", prefix = "<trim prefixOverrides='' suffixOverrides=','>", postfix = "</trim>"
            ) {
                //language=xml
                """
                    <if test="$parameterName.${it.simpleName} != null">${it.columnName.sqlQuote()}, </if>
                """.trimIndent()
            }
            val values = insertProperties.joinToString(
                separator = "", prefix = "<trim prefixOverrides='' suffixOverrides=','>", postfix = "</trim>"
            ) {
                //language=xml
                """
                    <if test="$parameterName.${it.simpleName} != null">${it.templateParameterStr(parameterName)}, </if>
                """.trimIndent()
            }
            //language=xml
            return """
                <insert>
                insert into ${crudEntity.dbFullyQualifiedName} ($columns)
                values ($values)
                </insert>
            """.trimIndent()
        }
    }

    open class Update(daoHandler: DaoHandler, method: LazyMethod) : CrudMethod(daoHandler, method) {

        val updateProperties: List<BeanProperty>

        init {
            returnUpdateCount = true
            if (crudEntity.primaryKey.isEmpty()) {
                throw HandlerException(
                    "The update method requires that the class specified by Dao.crudEntity must have primary key",
                )
            }
            updateProperties = crudEntity.allFields.filter {
                it.exist && it.supportUpdate && it !in crudEntity.primaryKey
            }
            if (updateProperties.isEmpty()) {
                throw HandlerException("The entity class specified by Dao.crudEntity has no property that can be used for update")
            }
        }

        open fun setClause(): String {
            return updateProperties.joinToString {
                "${it.columnName.sqlQuote()} = ${it.templateParameterStr(parameterName)}"
            }
        }

        override fun getTemplate(): String {
            val where = crudEntity.primaryKey.joinToString(" and ") {
                "${it.columnName.sqlQuote()} = ${it.templateParameterStr(parameterName)}"
            }
            //language=xml
            return """
                <update>
                update ${crudEntity.dbFullyQualifiedName}
                <set>
                ${setClause()}
                </set>
                where $where
                </update>
            """.trimIndent()
        }
    }

    class UpdateNonNullFields(daoHandler: DaoHandler, method: LazyMethod) : Update(daoHandler, method) {

        override fun setClause(): String {
            return updateProperties.joinToString(separator = "") {
                //language=xml
                """
                    <if test="$parameterName.${it.simpleName} != null">
                        ${it.columnName.sqlQuote()} = ${it.templateParameterStr(parameterName)},
                    </if>
                """.trimIndent()
            }
        }
    }

}
