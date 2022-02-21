package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.annotation.ResultMappings
import com.github.afezeria.freedao.processor.core.*
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.NoType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror

/**
 *
 */
class ResultHelper(val daoModel: DaoModel, val element: ExecutableElement) {
    //    var returnVoid = false
    val returnType: TypeMirror
    var containerType: DeclaredType? = null
    var itemType: DeclaredType
//    var mapKeyType: DeclaredType? = null
    var mapValueType: DeclaredType? = null

    var mappings: MutableList<MappingData> = mutableListOf()


    init {

        returnType = element.returnType.parameterized(
            daoModel.element.asType() as DeclaredType,
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

        initMapping()
    }

    /**
     * 初始化结果集映射关系
     */
    fun initMapping() {
        val resultMappings = element.getAnnotation(ResultMappings::class.java)?.apply {
            if (onlyCustomMapping && value.isEmpty()) {
                throw HandlerException("Invalid result mapping, value cannot be empty when onlyCustomMapping is true")
            }
            value.forEach {
                val resultTypeHandler = it.mirroredType { typeHandler }
                if (resultTypeHandler.findMethod("handle", Any::class.type) == null) {
                    throw HandlerException("Invalid ResultTypeHandler:${resultTypeHandler.typeName}, missing method:handle(Object.class)")
                }
            }
        }

        when {
            itemType.isCustomJavaBean() -> {
                val model = EntityObjectModel(itemType)
                //映射结果为java bean
                //处理构造器参数映射
                val constructor = (
                        itemType.asElement().enclosedElements.filter {
                            it is ExecutableElement && it.kind == ElementKind.CONSTRUCTOR && it.modifiers.contains(
                                Modifier.PUBLIC)
                        }.minByOrNull { (it as ExecutableElement).parameters.size }
                            ?: throw HandlerException("Return type $itemType must have a public constructor")
                        ) as ExecutableElement
                constructor.parameters.forEachIndexed { index, param ->

                    val prop =
                        model.properties.find {
                            it.name == param.simpleName.toString() && it.type.isSameType(param.asType().boxed())
                        }
                            ?: throw HandlerException("Constructor parameter name must be the same as field name:${itemType}.${param.simpleName}")
                    mappings += MappingData(prop.element, index)
                }

                //处理属性映射
                model.properties
                    .filter { mappings.none { m -> m.target == it.name } }
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
                    val customMappings = value.mapTo(mutableListOf()) {
                        //检查mapping注解的值是否合法
                        val prop =
                            model.properties.find { p -> p.name == it.target }
                                ?: throw HandlerException("${itemType.typeName} is missing the ${it.target} field")
                        val resultTypeHandler = it.mirroredType { typeHandler }
                            .apply {
                                if (!isSameType(ResultTypeHandler::class.type)
                                    && !findMethod("handle", Any::class.type)!!.returnType.isAssignable(prop.type)
                                ) {
                                    throw HandlerException("$this does not match field:[${prop.name}:${prop.type}]")
                                }
                            }
                        //上面已经检查了itemType是否包含名为it.target的属性，所以这里indexOfFirst返回值必定不为null
                        val autoMapping = mappings.removeAt(mappings.indexOfFirst { m -> m.target == it.target })
                        MappingData(
                            source = it.source,
                            target = it.target,
                            typeHandler = resultTypeHandler,
                            targetType = autoMapping.targetType,
                            constructorParameterIndex = autoMapping.constructorParameterIndex
                        )
                    }
                    if (onlyCustomMapping) {
                        mappings = customMappings
                    }
                }
            }
//            itemType.erasure().isAssignable(Map::class.type) -> {
//                //映射结果为map
//                resultMappings?.apply {
//                    val valueType = itemType.findTypeArgument(Map::class.type, "V")!!
//                    value.forEach {
//                        val resultTypeHandler = it.mirroredType { typeHandler }
//                            .apply {
//                                if (!isSameType(ResultTypeHandler::class.type)
//                                    && !findMethod("handle", Any::class.type)!!.returnType.isAssignable(valueType)
//                                ) {
//                                    throw HandlerException("$this does not match type:${valueType.typeName}")
//                                }
//                            }
//                        MappingData(it.source, it.target, resultTypeHandler)
//                    }
//                }
//            }
            else -> {
                val t = if (itemType.isAssignable(Map::class.type)) {
                    mapValueType!!
                } else {
                    itemType
                }
                //itemType既不是自定义bean也不是map时只能是单值类型，比如：Integer,LocalDateTime,String
                resultMappings?.apply {
                    value.forEach {
                        val resultTypeHandler = it.mirroredType { typeHandler }
                            .apply {
                                if (!isSameType(ResultTypeHandler::class.type)
                                    && !findMethod("handle", Any::class.type)!!.returnType.isAssignable(t)
                                ) {
                                    throw HandlerException("$this does not match type:$t")
                                }
                            }
                        MappingData(it.source, it.target, resultTypeHandler)
                    }
                }
            }
        }
    }

    val isStructuredItem: Boolean
        get() = itemType.isAssignable(Map::class) || itemType.isCustomJavaBean()

}