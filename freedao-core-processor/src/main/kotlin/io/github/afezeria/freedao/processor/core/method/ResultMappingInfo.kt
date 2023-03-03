package io.github.afezeria.freedao.processor.core.method

import io.github.afezeria.freedao.annotation.ResultMappings
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.processor.*

/**
 * 解析方法返回值和结果映射
 * @author afezeria
 */
interface ResultMappingInfo {
    /**
     * [LazyMethod]的returnType
     */
    val returnType: LazyType

    /**
     * 当返回类型为集合类型，值为集合类型的容器类型的实现类
     *
     * 否则为null
     *
     * 当容器类型为接口类型时按照以下规则选择实现类：
     * List -> ArrayList
     * Set -> HashSet
     * Collection -> ArrayList
     * other -> throw Exception
     */
    val returnTypeContainerType: LazyType?

    /**
     * 当返回类型为Map或Map的集合时，值为Map的值的类型
     * 否则为null
     */
    val returnTypeMapValueType: LazyType?

    /**
     * 当方法返回多行结果集时，返回类型为容器类型，该值为容器元素的类型
     *
     * 否则该值为返回结果的类型
     *
     * 当类型为Map的接口时值为HashMap，当类型为原始类型时值为原始类型的包装类
     *
     * 该类型不会是抽象的
     */
    val returnTypeItemType: LazyType

    /**
     * 同[itemType]，但是当类型为Map时，值为实际声明的类型，可能为抽象类或接口
     */
    val returnTypeOriginalItemType: LazyType

    /**
     * 结果映射规则
     */
    val mappings: MutableList<MappingData>

    companion object {
        data class TypeInfo(
            val itemType: LazyType,
            val originalItemType: LazyType,
            val containerType: LazyType? = null,
            val mapValueType: LazyType? = null,
        )

        operator fun invoke(method: LazyMethod): ResultMappingInfo {
            val typeInfo = determineType(method)
            val determineMappings = determineMappings(method, typeInfo)
            return object : ResultMappingInfo {
                override val returnType: LazyType
                    get() = method.returnType
                override val returnTypeContainerType: LazyType?
                    get() = typeInfo.containerType
                override val returnTypeMapValueType: LazyType?
                    get() = typeInfo.mapValueType
                override val returnTypeItemType: LazyType
                    get() = typeInfo.itemType
                override val returnTypeOriginalItemType: LazyType
                    get() = typeInfo.originalItemType
                override val mappings: MutableList<MappingData>
                    get() = determineMappings

            }
        }

        /**
         * 确定返回值类型
         *
         * @param method LazyMethod
         * @return TypeInfo
         */
        fun determineType(method: LazyMethod): TypeInfo {
            when (val returnType = method.returnType) {
                is NoType -> {
                    throw HandlerException("Invalid return type, cannot return void")
                }

                is PrimitiveType -> {
                    val boxedType = returnType.boxed()
                    return TypeInfo(
                        itemType = boxedType,
                        originalItemType = boxedType,
                    )
                }

                else -> {
                    val itemType: LazyType
                    val originalItemType: LazyType
                    var containerType: LazyType? = null
                    var mapValueType: LazyType? = null
                    //检查类型
                    if (returnType is CollectionType) {
                        //多行返回值
                        containerType = if (returnType.isAbstract) {
                            when {
                                returnType.isSameType(List::class) -> ArrayList::class
                                returnType.isSameType(Set::class) -> HashSet::class
                                returnType.isSameType(Collection::class) -> ArrayList::class
                                else -> throw HandlerException("Invalid return type")
                            }.typeLA
                        } else {
                            returnType.erasure()
                        }

                        originalItemType = returnType.elementType
                    } else {
                        //单行返回值
                        originalItemType = returnType
                    }
                    //结果集行类型为抽象类型时类型必须为Map
                    if (originalItemType.isAbstract && !originalItemType.erasure().isSameType(Map::class)) {
                        throw HandlerException("Invalid return type:$originalItemType, the abstract type of single row result can only be Map")
                    }
                    itemType = when (originalItemType) {
                        is CollectionType -> {
                            throw HandlerException("Invalid type argument:$originalItemType")
                        }

                        is MapType -> {
                            originalItemType.run {
                                if (!keyType.isSameType(String::class)) {
                                    throw HandlerException("Invalid type argument:${originalItemType.keyType}, the key type must be String")
                                }
                                mapValueType = valueType.apply {
                                    if (isAbstract) {
                                        throw HandlerException("Invalid type argument:$this, the value type cannot be abstract")
                                    }
                                }
                                if (isAbstract) {
                                    HashMap::class.typeLA
                                } else {
                                    this
                                }
                            }
                        }

                        else -> {
                            originalItemType
                        }
                    }.erasure()
                    return TypeInfo(itemType, originalItemType, containerType, mapValueType)
                }
            }
        }

        /**
         * 生成方法的映射规则
         *
         * @param method LazyMethod
         * @param typeInfo TypeInfo
         * @return MutableList<MappingData> 当方法返回类型不是 java bean 且方法上未添加[ResultMappings]时返回空列表
         */
        fun determineMappings(method: LazyMethod, typeInfo: TypeInfo): MutableList<MappingData> {
            val itemType = typeInfo.itemType
            var mappings: MutableList<MappingData> = mutableListOf()
            val resultMappings =
                method.getAnnotation(ResultMappings::class)?.also {
                    if (it.onlyCustomMapping && it.value.isEmpty()) {
                        throw HandlerException("Invalid result mapping, value cannot be empty when onlyCustomMapping is true")
                    }
                }
            itemType.asBeanType()?.apply {
                //对象映射规则
                mappings += defaultMappings
                resultMappings?.apply {
                    //如果方法上有声明映射规则，则用声明的规则替换掉根据实体类定义自动生成的规则
                    val customMappings = value.mapTo(mutableListOf()) { mapping ->
                        val index = mappings.indexOfFirst { it.target == mapping.target }
                        if (index == -1) {
                            throw HandlerException("${itemType.qualifiedName} is missing the ${mapping.target} field")
                        }
                        val autoMapping = mappings.removeAt(index)
                        val handlerType = mapping.wrapperType { typeHandler }
                            .throwIfNotResultTypeHandlerOrNotMatchType(autoMapping.targetTypeLA!!) {
                                "$this cannot handle ${autoMapping.target}:${autoMapping.targetTypeLA} field"
                            } ?: autoMapping.typeHandlerLA
                        MappingData(
                            source = mapping.source,
                            target = mapping.target,
                            typeHandlerLA = handlerType,
                            targetTypeLA = autoMapping.targetTypeLA,
                            constructorParameterIndex = autoMapping.constructorParameterIndex,
                        )
                    }
                    if (onlyCustomMapping) {
                        mappings = customMappings
                    } else {
                        mappings += customMappings
                    }
                }
            } ?: run {
                //Map/值类型映射规则
                val t = if (itemType.isAssignable(Map::class)) {
                    typeInfo.mapValueType!!
                } else {
                    itemType
                }
                //itemType既不是自定义bean也不是map时只能是单值类型，比如：Integer,LocalDateTime,String
                resultMappings?.apply {
                    value.forEach {
                        val handlerType = it.wrapperType { typeHandler }
                            .throwIfNotResultTypeHandlerOrNotMatchType(t) { "$this does not match $t type" }

                        mappings += MappingData(
                            source = it.source,
                            target = it.target,
                            typeHandlerLA = handlerType,
                            //这里原来是没有设置类型的
                            targetTypeLA = t,
                        )
                    }
                }
            }

            return mappings
        }

    }
}