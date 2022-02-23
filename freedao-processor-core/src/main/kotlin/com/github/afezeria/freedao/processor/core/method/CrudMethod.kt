package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.Long2IntegerResultHandler
import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.processor.core.*
import javax.lang.model.element.ExecutableElement


abstract class CrudMethod private constructor(element: ExecutableElement, daoHandler: DaoHandler) :
    MethodHandler(element, daoHandler) {

    val crudEntity: EntityObjectModel

    init {
        crudEntity = daoHandler.crudEntity
            ?: throw HandlerException("Method $name requires Dao.crudEntity to be specified")

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
                "insertSelective" -> InsertSelective(element, daoHandler)
                "update" -> Update(element, daoHandler)
                "updateSelective" -> UpdateSelective(element, daoHandler)
                "all" -> All(element, daoHandler)
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
                MappingData(source = "_cot",
                    target = "",
                    typeHandler = if (resultHelper.returnType.isSameType(Int::class)) {
                        Long2IntegerResultHandler::class.type
                    } else {
                        ResultTypeHandler::class.type
                    },
                    targetType = null,
                    constructorParameterIndex = -1
                )
        }

        override fun getTemplate(): String {
            //language=xml
            return """
                <select>
                select count(*) as _cot from ${crudEntity.dbFullyQualifiedName}
                </select>
            """.trimIndent()
        }
    }

    class Delete(element: ExecutableElement, daoHandler: DaoHandler) :
        CrudMethod(element, daoHandler) {
        init {
            returnUpdateCount = true
            if (crudEntity.primaryKey.isEmpty()) {
                throw HandlerException(
                    "The delete method requires that the Dao.crudEntity has primary key",
                )
            }
            crudEntity.primaryKey.forEach { requireParameter(it.type) }
        }

        override fun getTemplate(): String {
            val where = crudEntity.primaryKey.joinToString(separator = " and ") {
                "${it.column.name.sqlQuote()} = #{${it.name}}"
            }
            //language=xml
            return """
                <delete>
                delete from ${crudEntity.dbFullyQualifiedName} where $where
                </delete>
            """.trimIndent()
        }

    }

    open class Insert(element: ExecutableElement, daoHandler: DaoHandler) :
        CrudMethod(element, daoHandler) {
        val insertProperties: List<BeanProperty>

        init {
            returnUpdateCount = true
            requireParameter(crudEntity.type)
            insertProperties = crudEntity.properties
                .filter { it.column.run { exist && insert } }
            if (insertProperties.isEmpty()) {
                throw HandlerException("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
            }
        }

        override fun getTemplate(): String {
            val parameterName = parameters.find { it.type.isSameType(crudEntity.type) }!!.name

            //language=xml
            return """
                <insert>
                insert into ${crudEntity.dbFullyQualifiedName} (${insertProperties.joinToString { it.column.name.sqlQuote() }})
                values (${insertProperties.joinToString { "#{$parameterName.${it.name}}" }})
                </insert>
            """.trimIndent()
        }

    }

    class InsertSelective(element: ExecutableElement, daoHandler: DaoHandler) :
        Insert(element, daoHandler) {

        override fun getTemplate(): String {
            val parameterName = element.parameters[0].simpleName.toString()
            val columns = insertProperties.joinToString(
                separator = "",
                prefix = "<trim prefixOverrides='' postfixOverrides=','>",
                postfix = "</trim>") {
                //language=xml
                """
                    <if test="$parameterName.${it.name} != null">${it.column.name.sqlQuote()}, </if>
                """.trimIndent()
            }
            val values = insertProperties.joinToString(
                separator = "",
                prefix = "<trim prefixOverrides='' postfixOverrides=','>",
                postfix = "</trim>") {
                //language=xml
                """
                    <if test="$parameterName.${it.name} != null">#{$parameterName.${it.name}}, </if>
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
            requireParameter(crudEntity.type)
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

        override fun getTemplate(): String {
            val parameterName = element.parameters[0].simpleName.toString()
            val properties = crudEntity.getProperties()
                .filter { it.column.run { exist && update } }
            val set = properties.joinToString {
                "${it.column.name.sqlQuote()} = #{$parameterName.${it.name}}"
            }
            val where = crudEntity.primaryKey.joinToString(" and ") {
                "${it.column.name.sqlQuote()} = #{$parameterName.${it.name}}"
            }
            //language=xml
            return """
                <update>
                update ${crudEntity.dbFullyQualifiedName}
                set $set
                where $where
                </update>
            """.trimIndent()
        }
    }

    class UpdateSelective(element: ExecutableElement, daoHandler: DaoHandler) :
        Update(element, daoHandler) {

        override fun getTemplate(): String {
            val parameterName = element.parameters[0].simpleName.toString()
            val set = updateProperties.joinToString(separator = "", prefix = "<set>", postfix = "</set>") {
                //language=xml
                """
                    <if test="$parameterName.${it.name} != null">
                        ${it.column.name.sqlQuote()} = #{$parameterName.${it.name}},
                    </if>
                """.trimIndent()
            }
            val where = crudEntity.primaryKey.joinToString(" and ") {
                "${it.column.name.sqlQuote()} = #{$parameterName.${it.name}}"
            }
            //language=xml
            return """
                <update>
                update ${crudEntity.dbFullyQualifiedName}
                $set
                where $where
                </update>
            """.trimIndent()
        }
    }

    class All(element: ExecutableElement, daoHandler: DaoHandler) :
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
                </select>
            """.trimIndent()
        }
    }
}
