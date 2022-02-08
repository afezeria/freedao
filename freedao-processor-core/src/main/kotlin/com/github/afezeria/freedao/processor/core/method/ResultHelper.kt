package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.annotation.ResultMappings
import com.github.afezeria.freedao.processor.core.*
import com.squareup.javapoet.CodeBlock
import java.util.concurrent.ConcurrentMap
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.NoType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror

/**
 *
 */
class ResultHelper(element: ExecutableElement) {
    var returnVoid = false
    val returnType: TypeMirror
    var containerType: DeclaredType? = null
    lateinit var itemType: DeclaredType
    var mapKeyType: DeclaredType? = null
    var mapValueType: DeclaredType? = null

    var newContainerStatement: CodeBlock? = null
    var newItemStatement: CodeBlock? = null

    var mappings: MutableList<MappingData> = mutableListOf()


    init {
        returnType = element.returnType
        if (returnType is NoType) {
            returnVoid = true
        } else if (returnType is PrimitiveType) {
            itemType = returnType.boxed()
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
                    type
                }
                originalItemType = requireNotNull(type.findTypeArgument(Collection::class.type, "E"))
            } else {
                //单行返回值
                originalItemType = type
            }
            if (originalItemType.isAbstractType()) {
                //单行结果的类型为抽象类型必须为Map或ConcurrentMap
                if (!originalItemType.erasure().isSameType(Map::class) && !originalItemType.erasure()
                        .isSameType(ConcurrentMap::class)
                ) {
                    throw HandlerException("Invalid type argument:$originalItemType")
                }
                mapKeyType = requireNotNull(type.findTypeArgument(Map::class.type, "K")).run {
                    if (isSameType(Any::class) || isSameType(String::class)) {
                        String::class.type
                    } else {
                        throw HandlerException("Invalid type argument:$this")
                    }
                }
                mapValueType = requireNotNull(type.findTypeArgument(Map::class.type, "V")).run {
                    if (isSameType(Any::class) || isNotAbstractType()) {
                        this
                    } else {
                        throw HandlerException("Invalid type argument:$this")
                    }
                }
                itemType = if (originalItemType.erasure().isSameType(Map::class)) {
                    HashMap::class
                } else {
                    ConcurrentMap::class
                }.type
            } else {
                itemType = originalItemType
                when {
                    originalItemType.isAssignable(Collection::class) -> {
                        throw HandlerException("Invalid type argument:$originalItemType")
                    }
                    originalItemType.isAssignable(Map::class) -> {
                        mapKeyType = requireNotNull(type.findTypeArgument(Map::class.type, "K")).run {
                            if (isSameType(Any::class) || isSameType(String::class)) {
                                String::class.type
                            } else {
                                throw HandlerException("Invalid type argument:$this")
                            }
                        }
                        mapValueType = requireNotNull(type.findTypeArgument(Map::class.type, "V")).run {
                            if (isSameType(Any::class) || isNotAbstractType()) {
                                this
                            } else {
                                throw HandlerException("Invalid type argument:$this")
                            }
                        }
                    }
                    else -> {}
                }
            }

            containerType?.apply {
                val diamondStr = if ((asElement() as TypeElement).typeParameters.isEmpty()) {
                    ""
                } else {
                    "<>"
                }
                newContainerStatement =
                    CodeBlock.builder().addStatement("\$T list = new \$T$diamondStr()", type, containerType).build()
            }
            if (itemType.isAssignable(Map::class) || itemType.isCustomJavaBean()) {
                val diamondStr = if ((itemType.asElement() as TypeElement).typeParameters.isEmpty()) {
                    ""
                } else {
                    "<>"
                }
                newItemStatement =
                    CodeBlock.builder().addStatement("\$T list = new \$T$diamondStr()", originalItemType, itemType)
                        .build()
            }
        }

        //检查映射
        element.getAnnotation(ResultMappings::class.java)?.apply {
            if (!autoMapping && value.isEmpty()) {
                throw HandlerException("invalid result mapping, value cannot be empty when autoMapping is false")
            }
            containerType?.let { type ->
                if (type.isCustomJavaBean()) {
                    val constructor = type.asElement().enclosedElements
                        .asSequence()
                        .filter {
                            it is ExecutableElement && it.kind == ElementKind.CONSTRUCTOR && it.modifiers.contains(
                                Modifier.PUBLIC)
                        }.map { it as ExecutableElement to it.parameters.size }
                        .sortedBy { it.second }
                        .first().first
//                    type.asElement().enclosedElements.filter { it is  }
//                    constructor.parameters.

                }
            }
            mappings = value.mapTo(mutableListOf()) {
                MappingData(it.source, it.target, it.mirroredType { typeHandle })
            }
        }
    }

    val isStructuredItem: Boolean
        get() = itemType.isAssignable(Map::class) || itemType.isCustomJavaBean()


    /**
     * 存储[com.github.afezeria.freedao.annotation.Mapping]内容
     */
    data class MappingData(
        val source: String,
        val target: String,
        val typeHandler: DeclaredType,
        val targetType: DeclaredType? = null,
        val constructorParameter: Boolean = false,
    )

}