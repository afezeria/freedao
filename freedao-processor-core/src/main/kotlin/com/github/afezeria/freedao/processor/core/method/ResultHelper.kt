package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.processor.core.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.NoType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror

/**
 *
 */
class ResultHelper(val daoHandler: DaoHandler, val element: ExecutableElement) {
    //    var returnVoid = false
    val returnType: TypeMirror
    var containerType: DeclaredType? = null
    var itemType: DeclaredType

    var tooManyResultCheck = false

    //    var mapKeyType: DeclaredType? = null
    var mapValueType: DeclaredType? = null

    init {
        returnType = element.returnType.parameterized(
            daoHandler.element.asType() as DeclaredType,
            element.enclosingElement.asType().erasure() as DeclaredType
        )
        if (returnType is NoType) {
            throw HandlerException("Invalid return type, cannot return void")
        } else if (returnType is PrimitiveType) {
            itemType = returnType.boxed() as DeclaredType
        } else {
            //检查类型
            val type = returnType as DeclaredType
            val originalItemType: DeclaredType
            if (type.isAssignable(Collection::class)) {
                //多行返回值
                containerType = if (type.isAbstractType()) {
                    when {
                        type.erasure().isSameType(List::class) -> ArrayList::class
                        type.erasure().isSameType(Set::class) -> HashSet::class
                        type.erasure().isSameType(Collection::class) -> ArrayList::class
                        else -> throw HandlerException("Invalid return type")
                    }.type
                } else {
                    type.erasure() as DeclaredType
                }
                originalItemType = requireNotNull(type.findTypeArgument(Collection::class.type, "E"))
            } else {
                //单行返回值
                originalItemType = type
            }
            if (originalItemType.isAbstractType() && !originalItemType.erasure().isSameType(Map::class)) {
                throw HandlerException("Invalid return type:$originalItemType, the abstract type of single row result can only be Map")
            }
            itemType = when {
                originalItemType.isAssignable(Collection::class) -> {
                    throw HandlerException("Invalid type argument:$originalItemType")
                }
                originalItemType.isAssignable(Map::class) -> {
                    //map的key的类型必须为字符串
                    requireNotNull(originalItemType.findTypeArgument(Map::class.type, "K")).run {
                        if (isSameType(Any::class) || isSameType(String::class)) {
                            String::class.type
                        } else {
                            throw HandlerException("Invalid type argument:$this, the key type must be String")
                        }
                    }
                    mapValueType = requireNotNull(originalItemType.findTypeArgument(Map::class.type, "V")).run {
                        if (isNotAbstractType()) {
                            this
                        } else {
                            throw HandlerException("Invalid type argument:$this, the value type cannot be abstract")
                        }
                    }
                    if (originalItemType.isAbstractType()) {
                        HashMap::class.type
                    } else {
                        originalItemType
                    }
                }
                else -> {
                    originalItemType
                }
            }.erasure() as DeclaredType

        }
    }

    val isStructuredItem: Boolean
        get() = itemType.isAssignable(Map::class) || itemType.isCustomJavaBean()

}