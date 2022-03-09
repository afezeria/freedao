package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.Long2IntegerResultHandler
import com.github.afezeria.freedao.processor.core.*
import javax.lang.model.element.ExecutableElement


abstract class CrudMethod private constructor(element: ExecutableElement, daoHandler: DaoHandler) :
    MethodHandler(element, daoHandler) {

    val crudEntity: EntityObjectModel

    init {
        crudEntity = daoHandler.crudEntity
            ?: throw HandlerException("Method $name requires Dao.crudEntity to be specified")
        requireParameterByTypes(crudEntity.type)
    }

    val parameterName: String by lazy {
        requiredParameters[0].name
    }
    val where: String by lazy {
        crudEntity.properties.joinToString(separator = "\n") {
            //language=Xml
            """
                    <if test='${parameterName}.${it.name} != null'>
                    and ${it.column.name.sqlQuote()} = ${it.sqlParameterStr(parameterName)}
                    </if>
                """.trimIndent()
        }
    }


    companion object {
        operator fun invoke(
            element: ExecutableElement,
            daoHandler: DaoHandler,
        ): CrudMethod? {
            return when (element.simpleName.toString()) {
                "count" -> Count(element, daoHandler)
                "delete" -> Delete(element, daoHandler)
                "insert" -> Insert(element, daoHandler)
                "insertNonNullFields" -> InsertNonNullFields(element, daoHandler)
                "update" -> Update(element, daoHandler)
                "updateNonNullFields" -> UpdateNonNullFields(element, daoHandler)
                "list" -> ListMethod(element, daoHandler)
                else -> null
            }
        }
    }

    class Count(element: ExecutableElement, daoHandler: DaoHandler) : CrudMethod(element, daoHandler) {
        init {
            if (!resultHelper.returnType.isSameType(Int::class)
                && !resultHelper.returnType.isSameType(Long::class)
            ) {
                throw HandlerException("The return type of count method must be Integer or Long")
            }
            mappings +=
                MappingData(
                    source = "_cot",
                    target = "",
                    typeHandler = Long2IntegerResultHandler::class.type.takeIf { resultHelper.returnType.isSameType(Int::class) },
                    targetType = Int::class.type.takeIf { resultHelper.returnType.isSameType(Int::class) },
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

    class Delete(element: ExecutableElement, daoHandler: DaoHandler) :
        CrudMethod(element, daoHandler) {
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

    class ListMethod(element: ExecutableElement, daoHandler: DaoHandler) :
        CrudMethod(element, daoHandler) {

        init {
            if (
                !(resultHelper.returnType.isAssignable(Collection::class.type) &&
                        resultHelper.itemType.isSameType(crudEntity.type))
            ) {
                throw HandlerException("The return type must be assignable to Collection<${crudEntity.type.typeName}>")
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

    open class Insert(element: ExecutableElement, daoHandler: DaoHandler) :
        CrudMethod(element, daoHandler) {
        val insertProperties: List<BeanProperty>

        init {
            returnUpdateCount = true
            insertProperties = crudEntity.properties
                .filter { it.column.run { exist && insert } }
            if (insertProperties.isEmpty()) {
                throw HandlerException("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
            }
        }

        override fun getTemplate(): String {

            //language=xml
            return """
                <insert>
                insert into ${crudEntity.dbFullyQualifiedName} (${insertProperties.joinToString { it.column.name.sqlQuote() }})
                values (${insertProperties.joinToString { it.sqlParameterStr(parameterName) }})
                </insert>
            """.trimIndent()
        }

    }

    class InsertNonNullFields(element: ExecutableElement, daoHandler: DaoHandler) :
        Insert(element, daoHandler) {

        override fun getTemplate(): String {
            val columns = insertProperties.joinToString(
                separator = "",
                prefix = "<trim prefixOverrides='' suffixOverrides=','>",
                postfix = "</trim>"
            ) {
                //language=xml
                """
                    <if test="$parameterName.${it.name} != null">${it.column.name.sqlQuote()}, </if>
                """.trimIndent()
            }
            val values = insertProperties.joinToString(
                separator = "",
                prefix = "<trim prefixOverrides='' suffixOverrides=','>",
                postfix = "</trim>"
            ) {
                //language=xml
                """
                    <if test="$parameterName.${it.name} != null">${it.sqlParameterStr(parameterName)}, </if>
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

    open class Update(element: ExecutableElement, daoHandler: DaoHandler) :
        CrudMethod(element, daoHandler) {

        val updateProperties: List<BeanProperty>

        init {
            returnUpdateCount = true
            if (crudEntity.primaryKey.isEmpty()) {
                throw HandlerException(
                    "The update method requires that the class specified by Dao.crudEntity must have primary key",
                )
            }
            updateProperties = crudEntity.properties
                .filter {
                    it.column.run { exist && update } && it !in crudEntity.primaryKey
                }
            if (updateProperties.isEmpty()) {
                throw HandlerException("The entity class specified by Dao.crudEntity has no property that can be used for update")
            }
        }

        open fun setClause(): String {
            return updateProperties.joinToString {
                "${it.column.name.sqlQuote()} = ${it.sqlParameterStr(parameterName)}"
            }
        }

        override fun getTemplate(): String {
            val where = crudEntity.primaryKey.joinToString(" and ") {
                "${it.column.name.sqlQuote()} = ${it.sqlParameterStr(parameterName)}"
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

    class UpdateNonNullFields(element: ExecutableElement, daoHandler: DaoHandler) :
        Update(element, daoHandler) {

        override fun setClause(): String {
            return updateProperties.joinToString(separator = "") {
                //language=xml
                """
                    <if test="$parameterName.${it.name} != null">
                        ${it.column.name.sqlQuote()} = ${it.sqlParameterStr(parameterName)},
                    </if>
                """.trimIndent()
            }
        }
    }

}
