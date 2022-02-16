package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.Long2IntegerResultHandler
import com.github.afezeria.freedao.processor.core.*
import javax.lang.model.element.ExecutableElement
import kotlin.reflect.full.primaryConstructor


sealed class CrudMethod(element: ExecutableElement, daoModel: DaoModel) :
    MethodModel(element, daoModel) {

    lateinit var crudEntity: EntityObjectModel

    init {
        daoModel.crudEntity?.let {
            crudEntity = it
        } ?: run {
            throw HandlerException(
                "Method $name requires that the crudEntity attribute to be specified in the Dao annotation of the interface",
            )
        }
    }


    companion object {
        operator fun invoke(
            element: ExecutableElement,
            daoModel: DaoModel,
        ): CrudMethod? {
            return CrudMethod::class.sealedSubclasses.find {
                it.simpleName!!.replaceFirstChar { it.lowercase() } == element.simpleName.toString()
            }?.let {
                it.primaryConstructor!!.call(element, daoModel)
            }
        }

    }
}

class Count(element: ExecutableElement, daoModel: DaoModel) : CrudMethod(element, daoModel) {
    init {
        if (!resultHelper.returnType.isSameType(Int::class)
            && !resultHelper.returnType.isSameType(Long::class)
        ) {
            throw HandlerException("The return type of count method must be Integer or Long")
        }
        resultHelper.mappings +=
            MappingData(source = "_cot",
                target = "",
                typeHandler = Long2IntegerResultHandler::class.type,
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

class Delete(element: ExecutableElement, daoModel: DaoModel) :
    CrudMethod(element, daoModel) {
    init {
        returnUpdateCount = true
        if (crudEntity.primaryKey.isEmpty()) {
            throw HandlerException(
                "delete method requires that the class specified by Dao.crudEntity must have primary key",
            )
        }
        crudEntity.primaryKey.forEach { requireParameter(it.type, it.name) }
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

class Insert(element: ExecutableElement, daoModel: DaoModel) :
    CrudMethod(element, daoModel) {
    var insertProperties: List<BeanProperty>

    init {
        returnUpdateCount = true
        requireParameter(crudEntity.typeMirror)
        insertProperties = crudEntity.getProperties()
            .filter { it.column.run { exist && insert } }
        if (insertProperties.isEmpty()) {
            throw HandlerException("crudEntity has no property to insert")
        }
    }

    override fun getTemplate(): String {
        val parameterName = parameters.find { it.model.typeMirror.isSameType(crudEntity.type) }!!.name

        //language=xml
        return """
                <insert>
                insert into ${crudEntity.dbFullyQualifiedName} (${insertProperties.joinToString { it.column.name.sqlQuote() }})
                values (${insertProperties.joinToString { "#{$parameterName.${it.name}}" }})
                </insert>
            """.trimIndent()
    }

}

class InsertSelective(element: ExecutableElement, daoModel: DaoModel) :
    CrudMethod(element, daoModel) {

    var insertProperties: List<BeanProperty>


    init {
        returnUpdateCount = true
        requireParameter(crudEntity.typeMirror)
        insertProperties = crudEntity.getProperties()
            .filter { it.column.run { exist && insert } }
        if (insertProperties.isEmpty()) {
            throw HandlerException("crudEntity has no property to insert")
        }

    }

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

class Update(element: ExecutableElement, daoModel: DaoModel) :
    CrudMethod(element, daoModel) {

    var updateProperties: List<BeanProperty>

    init {
        returnUpdateCount = true
        requireParameter(crudEntity.typeMirror)
        if (crudEntity.primaryKey.isEmpty()) {
            throw HandlerException(
                "update method requires that the class specified by Dao.crudEntity must have primary key",
            )
        }
        updateProperties = crudEntity.getProperties()
            .filter { it.column.run { exist && update } }
        if (updateProperties.isEmpty()) {
            throw HandlerException("crudEntity has no property to update")
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

class UpdateSelective(element: ExecutableElement, daoModel: DaoModel) :
    CrudMethod(element, daoModel) {

    var updateProperties: List<BeanProperty>

    init {
        returnUpdateCount = true
        requireParameter(crudEntity.typeMirror)
        if (crudEntity.primaryKey.isEmpty()) {
            throw HandlerException(
                "update method requires that the class specified by Dao.crudEntity must have primary key",
            )
        }
        updateProperties = crudEntity.getProperties()
            .filter { it.column.run { exist && update } }
        if (updateProperties.isEmpty()) {
            throw HandlerException("crudEntity has no property to update")
        }
    }

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

class All(element: ExecutableElement, daoModel: DaoModel) :
    CrudMethod(element, daoModel) {

    init {
        if (!resultHelper.returnType.isAssignable(Collection::class.type(crudEntity.type))
        ) {
            throw HandlerException("The return type must be assignable to Collection<${crudEntity.type.typeName}>")
        }
    }

    override fun getTemplate(): String {

        //language=xml
        return """
                <select>
                select ${resultHelper.mappings.joinToString { it.source }} from ${crudEntity.dbFullyQualifiedName}
                </select>
            """.trimIndent()
    }
}