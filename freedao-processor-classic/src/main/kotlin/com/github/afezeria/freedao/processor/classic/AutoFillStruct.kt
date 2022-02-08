package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.*
import com.github.afezeria.freedao.processor.core.method.MethodModel
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
        private fun findAutoFillParameter(method: MethodModel): Triple<Int, DeclaredType?, DeclaredType>? {
            return method.element.parameters.run {
                indexOfFirst {
                    it.asType().run {
                        this is DeclaredType && (isCustomJavaBean() || (isAssignable(Collection::class) && findTypeArgument(
                            Collection::class.type,
                            "E")!!.isCustomJavaBean()))
                    }
                }.takeIf { it != -1 }?.let { idx ->
                    val type = get(idx).asType() as DeclaredType
                    if (type.isCustomJavaBean()) {
                        Triple(idx, null, type)
                    } else {
                        Triple(idx, type, type.findTypeArgument(Collection::class.type, "E")!!)
                    }
                }
            }
        }

        fun validation(method: MethodModel) {
            findAutoFillParameter(method)?.let { (_, _, type) ->
                type.asElement().enclosedElements.forEach { element ->
                    if (element is VariableElement && element.getAnnotation(AutoFill::class.java) != null && !element.hasSetter()) {
                        throw HandlerException("AutoFill property must have setter method")
                    }
                }
            }
        }

        operator fun invoke(method: MethodModel): AutoFillStruct? {
            return findAutoFillParameter(method)?.run {
                AutoFillStruct(first, second, third).takeIf { it.dbAutoFillProperties.isNotEmpty() }
            }
        }
    }
}
