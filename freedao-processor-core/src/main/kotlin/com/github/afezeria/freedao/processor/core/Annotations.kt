package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.DefaultEnumTypeHandler
import com.github.afezeria.freedao.annotation.Column
import com.github.afezeria.freedao.annotation.ResultMappings
import com.github.afezeria.freedao.processor.core.method.ResultHelper
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
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
    val resultTypeHandle: DeclaredType?
    val parameterTypeHandle: DeclaredType?

    init {
        val column = element.getAnnotation(Column::class.java)
        val fieldType = element.asType()
        column.let {
            name = it?.name?.takeIf { it.isNotBlank() } ?: element.simpleName.toString().toSnakeCase()
            exist = it?.exist ?: true
            insert = it?.insert ?: true
            update = it?.update ?: true
            resultTypeHandle =
                it?.mirroredType { resultTypeHandle }
                    ?.isResultTypeHandlerAndMatchType(element.asType()) {
                        "The result type handler $this and the type of field ${element.simpleName} do not match"
                    } ?: DefaultEnumTypeHandler::class.type.takeIf { fieldType.isAssignable(Enum::class.type) }
            parameterTypeHandle = it?.mirroredType { parameterTypeHandle }
                ?.isParameterTypeHandlerAndMatchType(element.asType()) {
                    "The parameter type handler $this and the type of field ${element.simpleName} do not match"
                }?.first ?: DefaultEnumTypeHandler::class.type.takeIf { fieldType.isAssignable(Enum::class.type) }
        }
    }
}

object ResultMappingsAnn {
    fun getMappings(methodElement: ExecutableElement, resultHelper: ResultHelper): MutableList<MappingData> {
        val resultMappings = methodElement.getAnnotation(ResultMappings::class.java)?.apply {
            if (onlyCustomMapping && value.isEmpty()) {
                throw HandlerException("Invalid result mapping, value cannot be empty when onlyCustomMapping is true")
            }
        }
        val itemType = resultHelper.itemType
        var mappings: MutableList<MappingData> = mutableListOf()
        when {
            itemType.isCustomJavaBean() -> {
                val model = BeanObjectModel(itemType)
                //映射结果为java bean
                //处理构造器参数映射
                val constructor = (
                        itemType.asElement().enclosedElements.filter {
                            it is ExecutableElement && it.kind == ElementKind.CONSTRUCTOR && it.modifiers.contains(
                                Modifier.PUBLIC
                            )
                        }.minByOrNull { (it as ExecutableElement).parameters.size }
                            ?: throw HandlerException("Return type $itemType must have a public constructor")
                        ) as ExecutableElement
                constructor.parameters.forEachIndexed { index, param ->

                    val prop =
                        model.properties.find {
                            it.name == param.simpleName.toString() && it.type.boxed().isSameType(param.asType().boxed())
                        }
                            ?: throw HandlerException("Constructor parameter name must be the same as field name:${itemType}.${param.simpleName}")
                    mappings += MappingData(prop.element, index)
                }

                //处理属性映射
                model.properties
                    .filter { it.hasSetter && mappings.none { m -> m.target == it.name } }
                    .forEach {
                        mappings += MappingData(
                            source = it.column.name,
                            target = it.name,
                            typeHandler = it.column.resultTypeHandle,
                            targetType = it.type.boxed() as DeclaredType,
                            constructorParameterIndex = -1
                        )
                    }


                resultMappings?.apply {
                    val customMappings = value.mapNotNullTo(mutableListOf()) {
                        //检查mapping注解的值是否合法
                        val prop =
                            model.properties.find { p -> p.name == it.target }
                                ?: throw HandlerException("${itemType.typeName} is missing the ${it.target} field")
                        val handlerType = it.mirroredType { typeHandler }
                            .isResultTypeHandlerAndMatchType(prop.type) {
                                "$this cannot handle ${prop.name}:${prop.type} field"
                            }

                        mappings.indexOfFirst { m -> m.target == it.target }
                            //如果索引为-1表示该属性没有setter方法且不在构造器参数中
                            .takeIf { it != -1 }
                            ?.let { idx ->
                                val autoMapping = mappings.removeAt(idx)
                                MappingData(
                                    source = it.source,
                                    target = it.target,
                                    typeHandler = handlerType,
                                    targetType = autoMapping.targetType,
                                    constructorParameterIndex = autoMapping.constructorParameterIndex
                                )
                            }
                    }
                    if (onlyCustomMapping) {
                        mappings = customMappings
                    } else {
                        mappings += customMappings
                    }
                }
            }
            else -> {
                val t = if (itemType.isAssignable(Map::class.type)) {
                    resultHelper.mapValueType!!
                } else {
                    itemType
                }
                //itemType既不是自定义bean也不是map时只能是单值类型，比如：Integer,LocalDateTime,String
                resultMappings?.apply {
                    value.forEach {
                        val handlerType = it.mirroredType { typeHandler }
                            .isResultTypeHandlerAndMatchType(t) {
                                "$this does not match $t type"
                            }

                        mappings += MappingData(
                            source = it.source,
                            target = it.target,
                            typeHandler = handlerType
                        )
                    }
                }
            }
        }
        return mappings
    }
}

/**
 * 存储[com.github.afezeria.freedao.annotation.Mapping]内容
 * @property source resultSet列名
 * @property target bean类字段名
 * @property typeHandler 结果处理器类型
 * @property targetType 字段类型
 * @property constructorParameterIndex 在构造器参数中的位置，-1表示该字段使用setter方法设置
 */
data class MappingData(
    var source: String,
    val target: String,
    var typeHandler: DeclaredType?,
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
                targetType = element.asType().boxed() as DeclaredType,
                constructorParameterIndex = constructorParameterIndex
            )
        }
    }
}
