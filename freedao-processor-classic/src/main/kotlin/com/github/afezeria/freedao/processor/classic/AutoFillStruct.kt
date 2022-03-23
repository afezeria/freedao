package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.*
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.github.afezeria.freedao.runtime.classic.AutoFill
import com.github.afezeria.freedao.runtime.classic.DbGenerator
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

/**
 * @author afezeria
 */
class AutoFillStruct private constructor(
    /**
     * 方法参数在参数列表中的位置
     */
    val index: Int,
    /**
     * 如果参数是集合时集合的类型
     */
    val collectionType: DeclaredType?,
    /**
     * 对象参数的类型
     */
    val type: DeclaredType,
) {
    val isCollection: Boolean = collectionType != null
    val autoFillProperties: List<BeanProperty> = type.asElement().enclosedElements.filter {
        it.hasSetter() && it.getAnnotation(AutoFill::class.java) != null
//            ?.takeIf { !it.before && it.mirroredType { generator }.isSameType(ValueGenerator::class) } != null
    }.map {
        BeanProperty(it as VariableElement)
    }

    companion object {
        /**
         * 从方法参数中查找第一个可以数据库生成字段填充的对象
         * @param method MethodModel
         * @return Triple<Int, DeclaredType?, DeclaredType>? first:在参数中的位置，second：容器类型，third：对象类型
         */
        private fun findAutoFillParameter(method: MethodHandler): AutoFillStruct? {
            return method.parameters.indexOfFirst {
                it.type.run {
                    this is DeclaredType && (
                            isCustomJavaBean() || (
                                    isAssignable(Collection::class)
                                            && findTypeArgument(Collection::class.type, "E").isCustomJavaBean()
                                    )
                            )
                }
            }.takeIf { it != -1 }?.let { idx ->
                val type = method.parameters[idx].type as DeclaredType
                if (type.isCustomJavaBean()) {
                    //单对象更新或插入
                    AutoFillStruct(idx, null, type)
                } else {
                    //批量更新或插入
                    AutoFillStruct(idx, type, type.findTypeArgument(Collection::class.type, "E"))
                }
            }
        }

        fun validation(method: MethodHandler) {
            findAutoFillParameter(method)?.apply {
                type.asElement().enclosedElements.forEach { element ->
                    if (element is VariableElement) {
                        val autoFillAnn = element.getAnnotation(AutoFill::class.java)
                        if (autoFillAnn != null) {
                            if (!element.hasSetter()) {
                                throw HandlerException("AutoFill property must have setter method")
                            }
                            checkGenerator(autoFillAnn.mirroredType { generator })
                        }
                    }
//                    if (element is VariableElement && element.getAnnotation(AutoFill::class.java) != null && !element.hasSetter()) {
//                        throw HandlerException("AutoFill property must have setter method")
//                    }
                }
            }
        }

        private fun checkGenerator(type: TypeMirror) {
            if (type !is DeclaredType) {
                throw HandlerException("AutoFill.generator must be Object")
            }
            if (type.isSameType(DbGenerator::class)) {
                return
            }
            type.asElement().enclosedElements
                .find {
                    it is ExecutableElement
                            && it.kind == ElementKind.METHOD
                            && it.simpleName.toString() == "gen"
                            && it.parameters.size == 3
                            && it.parameters[0].asType().isSameType(Any::class.type)
                            && it.parameters[1].asType().isSameType(String::class.type)
                            && it.parameters[2].asType()
                        .isSameType(Class::class.type(typeUtils.getWildcardType(null, null)))
                            && it.returnType.isSameType(Any::class.type)
                            && it.modifiers.containsAll(listOf(Modifier.PUBLIC, Modifier.STATIC))
                } as ExecutableElement?
                ?: throw HandlerException("Invalid generator:${type}, missing method:public static Object gen(Object, String, Class<?>)")

        }

        operator fun invoke(method: MethodHandler): AutoFillStruct? {
            return findAutoFillParameter(method)?.takeIf { it.autoFillProperties.isNotEmpty() }
        }
    }
}
