package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.*
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.github.afezeria.freedao.runtime.classic.AutoFill
import com.github.afezeria.freedao.runtime.classic.ValueGenerator
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType

/**
 *
 * @author afezeria
 */
class AutoFillStruct private constructor(
    val index: Int,
    val collectionType: DeclaredType?,
    val type: DeclaredType,
) {
    val isCollection: Boolean = collectionType != null
    val dbAutoFillProperties: List<BeanProperty> = type.asElement().enclosedElements.filter {
        it.hasSetter() && it.getAnnotation(AutoFill::class.java)
            ?.takeIf { !it.before && it.mirroredType { generator }.isSameType(ValueGenerator::class) } != null
    }.map {
        BeanProperty(it as VariableElement)
    }

    companion object {
        /**
         * 从方法参数中查找第一个需要用数据库生成字段填充的对象
         * @param method MethodModel
         * @return Triple<Int, DeclaredType?, DeclaredType>? first:在参数中的位置，second：容器类型，third：对象类型
         */
        private fun findAutoFillParameter(method: MethodHandler): AutoFillStruct? {
            return method.parameters.indexOfFirst {
                it.type.run {
                    this is DeclaredType
                            && (
                            isCustomJavaBean()
                                    || (isAssignable(Collection::class)
                                    && findTypeArgument(Collection::class.type, "E")!!.isCustomJavaBean())
                            )
                }
            }.takeIf { it != -1 }?.let { idx ->
                val type = method.parameters[idx].type as DeclaredType
                if (type.isCustomJavaBean()) {
                    AutoFillStruct(idx, null, type)
                } else {
                    AutoFillStruct(idx, type, type.findTypeArgument(Collection::class.type, "E")!!)
                }
            }
        }

        fun validation(method: MethodHandler) {
            findAutoFillParameter(method)?.apply {
                type.asElement().enclosedElements.forEach { element ->
                    if (element is VariableElement && element.getAnnotation(AutoFill::class.java) != null && !element.hasSetter()) {
                        throw HandlerException("AutoFill property must have setter method")
                    }
                }
            }
        }

        operator fun invoke(method: MethodHandler): AutoFillStruct? {
            return findAutoFillParameter(method)?.takeIf { it.dbAutoFillProperties.isNotEmpty() }
        }
    }
}
