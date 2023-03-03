package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.classic.runtime.AutoFill
import io.github.afezeria.freedao.classic.runtime.DbGenerator
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition
import io.github.afezeria.freedao.processor.core.processor.*

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
    val collectionType: LazyType?,
    /**
     * 对象参数的类型
     */
    val type: LazyType,
) {

    /**
     * 填充目标类型是集合类型
     */
    val isCollection: Boolean = collectionType != null
    val autoFillProperties: List<BeanProperty> = type.allFields.filter {
        it.hasSetter() && it.annotationNames.contains(AutoFill::class.simpleName)
    }.map {
        BeanProperty(it)
    }

    companion object {
        /**
         * 从方法参数中查找第一个可以数据库生成字段填充的对象
         * @param method MethodModel
         * @return Triple<Int, DeclaredType?, DeclaredType>? first:在参数中的位置，second：容器类型，third：对象类型
         */
        private fun findAutoFillParameter(method: AbstractMethodDefinition): AutoFillStruct? {
            return method.parameters.firstOrNull {
                it.type.run {
                    isBeanType() || (
                            isAssignable(Collection::class)
                                    && findTypeArgument(Collection::class.typeLA, "E").isBeanType()
                            )
                }
            }?.let {
                val type = it.type
                if (type.isBeanType()) {
                    //单对象更新或插入
                    AutoFillStruct(it.index, null, type)
                } else {
                    //批量更新或插入
                    AutoFillStruct(it.index, type, type.findTypeArgument(Collection::class.typeLA, "E"))
                }
            }
        }

        fun validation(method: AbstractMethodDefinition) {
            findAutoFillParameter(method)?.apply {
                type.allFields.forEach {
                    val annotation = it.getAnnotation(AutoFill::class)
                    if (annotation != null) {
                        if (!it.hasSetter()) {
                            throw HandlerException("AutoFill property must have setter method")
                        }
                        checkGenerator(typeService.getMirroredType(annotation) { generator })
                    }

                }
            }
        }

        private fun checkGenerator(type: LazyType) {
            if (type.isSameType(DbGenerator::class)) {
                return
            }
            type.declaredMethods.find {
                it.simpleName == "gen"
                        && it.parameters.size == 3
                        && it.parameters[0].type.isSameType(Any::class)
                        && it.parameters[1].type.isSameType(String::class)
                        && it.parameters[2].type.isSameType(
                    Class::class.typeLA(
                        typeService.getWildcardType(
                            null,
                            null
                        )
                    )
                )
                        && it.returnType.isSameType(Any::class)
                        && it.modifiers.containsAll(listOf(Modifier.PUBLIC, Modifier.STATIC))
            }
                ?: throw HandlerException("Invalid generator:${type}, missing method:public static Object gen(Object, String, Class<?>)")
        }

        operator fun invoke(method: AbstractMethodDefinition): AutoFillStruct? {
            return findAutoFillParameter(method)?.takeIf { it.autoFillProperties.isNotEmpty() }
        }
    }
}
