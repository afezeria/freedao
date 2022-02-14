package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.annotation.Column
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType

/**
 * @author afezeria
 */

class ColumnAnn(element: VariableElement) {
    val name: String
    val exist: Boolean
    val insert: Boolean
    val update: Boolean
    val resultTypeHandle: DeclaredType

    init {
        val column = element.getAnnotation(Column::class.java)
        column.let {
            name = it?.name?.takeIf { it.isNotBlank() } ?: element.simpleName.toString().toSnakeCase()
            exist = it?.exist ?: true
            insert = it?.insert ?: true
            update = it?.update ?: true
            resultTypeHandle = it?.mirroredType { resultTypeHandle } ?: ResultTypeHandler::class.type
        }
        //检查结果处理器和字段是否匹配
        if (!resultTypeHandle.isSameType(ResultTypeHandler::class.type)) {
            val handleMethod = resultTypeHandle.findMethod("handle", Any::class.type)
                ?.takeIf { it.modifiers.containsAll(listOf(Modifier.STATIC, Modifier.PUBLIC)) }
                ?: throw HandlerException("Invalid ResultTypeHandler:${resultTypeHandle.typeName}, cannot find method:handle(Object.class)")
            if (!handleMethod.returnType.isAssignable(element.asType())) {
                throw HandlerException("${resultTypeHandle.typeName} cannot handle field ${element.simpleName}:${element.asType()}, ${handleMethod.returnType} cannot assignable ${element.asType()}")
            }

        }
    }
}

/**
 * 存储[com.github.afezeria.freedao.annotation.Mapping]内容
 */
class MappingData(
    val source: String,
    val target: String,
    val typeHandler: DeclaredType,
    val targetType: DeclaredType? = null,
    val constructorParameterIndex: Int = -1,
) {

    companion object {
        operator fun invoke(
            element: VariableElement,
            constructorParameterIndex: Int = -1,
        ): MappingData {
            val columnAnn = ColumnAnn(element)
            return MappingData(
                source = columnAnn.name,
                target = element.simpleName.toString(),
                typeHandler = columnAnn.resultTypeHandle,
                targetType = element.asType() as DeclaredType,
                constructorParameterIndex = constructorParameterIndex
            )
        }
    }
}
