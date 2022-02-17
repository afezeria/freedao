package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.annotation.ResultMappings
import com.github.afezeria.freedao.processor.core.*
import com.squareup.javapoet.CodeBlock
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.NoType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror

/**
 *
 */
class ResultHelper(val daoModel: DaoModel, val element: ExecutableElement) {
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

        returnType = element.returnType.parameterized(
            daoModel.element.asType() as DeclaredType,
            element.enclosingElement.asType().erasure() as DeclaredType
        )
        if (returnType is NoType) {
            returnVoid = true
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
                    type
                }
                originalItemType = requireNotNull(type.findTypeArgument(Collection::class.type, "E"))
            } else {
                //单行返回值
                originalItemType = type
            }
            if (originalItemType.isAbstractType()) {
                //单行结果的类型为抽象类型时必须为Map
                if (!originalItemType.erasure().isSameType(Map::class)
                ) {
                    throw HandlerException("Invalid return type:$originalItemType, the abstract type of single row result can only be Map")
                }
                mapKeyType = requireNotNull(originalItemType.findTypeArgument(Map::class.type, "K")).run {
                    if (isSameType(Any::class) || isSameType(String::class)) {
                        String::class.type
                    } else {
                        throw HandlerException("Invalid type argument:$this, key type must be String")
                    }
                }
                mapValueType = requireNotNull(originalItemType.findTypeArgument(Map::class.type, "V")).run {
                    if (isNotAbstractType()) {
                        this
                    } else {
                        throw HandlerException("Invalid type argument:$this, value type cannot be abstract")
                    }
                }
                itemType = HashMap::class.type
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

        initMapping()
    }

    /**
     * 初始化结果集映射关系
     */
    fun initMapping() {
        val resultMappings = element.getAnnotation(ResultMappings::class.java)?.apply {
            if (overrideAutoMapping && value.isEmpty()) {
                throw HandlerException("invalid result mapping, value cannot be empty when overrideAutoMapping is true")
            }
        }
        when {
            itemType.isCustomJavaBean() -> {
                //映射结果为java bean
                val constructor = (itemType.asElement().enclosedElements.filter {
                    it is ExecutableElement && it.kind == ElementKind.CONSTRUCTOR && it.modifiers.contains(Modifier.PUBLIC)
                }.minByOrNull { (it as ExecutableElement).parameters.size }
                    ?: throw HandlerException("return type $itemType must have public constructor")) as ExecutableElement
                constructor.parameters.forEachIndexed { index, param ->
                    val field = itemType.findField(param.simpleName.toString(), param.asType())
                        ?: throw HandlerException("Constructor parameter name must be the same as field name.${itemType.simpleName},${param.simpleName}")
                    mappings += MappingData(field, index)
                }
                val constructorMappings = mutableListOf(*mappings.toTypedArray())

                itemType.asElement().enclosedElements
                    //过滤掉在构造器参数中存在的属性
                    .filter { it is VariableElement && it.hasSetter() && mappings.none { m -> m.target == it.simpleName.toString() } }
                    .forEach {
                        it as VariableElement
                        mappings += MappingData(it)
                    }
                resultMappings?.apply {
                    if (overrideAutoMapping) {
                        mappings.clear()
                    }
                    value.map {
                        //检查mapping注解的值是否合法
                        val field = itemType.findField(it.target)
                            ?: throw HandlerException("${itemType.typeName} is missing the ${it.target} field")
                        val resultTypeHandler = it.mirroredType { typeHandler }
                        if (!resultTypeHandler.isSameType(ResultTypeHandler::class.type)) {
                            val handleMethod = it.mirroredType { typeHandler }.findMethod("handle", Any::class.type)
                                ?: throw HandlerException("Invalid ResultTypeHandler:${resultTypeHandler.typeName}, cannot find method:handle(Object.class)")
                            if (!handleMethod.returnType.isAssignable(field.asType())) {
                                throw HandlerException("${resultTypeHandler.typeName} does not match field:[${field.simpleName}:${field.asType().typeName}]")
                            }
                        }
                        //上面已经检查了itemType是否包含名为it.target的属性，所以这里indexOfFirst返回值必定不为null
                        val autoMapping = mappings.removeAt(mappings.indexOfFirst { m -> m.target == it.target })
                        mappings += MappingData(
                            source = it.source,
                            target = it.target,
                            typeHandler = it.mirroredType { typeHandler },
                            targetType = autoMapping.targetType,
                            constructorParameterIndex = constructorMappings.find { m -> m.target == it.target }?.constructorParameterIndex
                                ?: -1
                        )
                    }
                }
            }
            itemType.erasure().isAssignable(Map::class.type) -> {
                //映射结果为map
                resultMappings?.apply {
                    val valueType = itemType.findTypeArgument(Map::class.type, "V")!!
                    value.forEach {
                        val resultTypeHandler = it.mirroredType { typeHandler }
                        if (!resultTypeHandler.isSameType(ResultTypeHandler::class.type)) {
                            val handleMethod = it.mirroredType { typeHandler }.findMethod("handle", Any::class.type)
                                ?: throw HandlerException("Invalid ResultTypeHandler:${resultTypeHandler.typeName}, cannot find method:handle(Object.class)")
                            if (!handleMethod.returnType.isAssignable(valueType)) {
                                throw HandlerException("${resultTypeHandler.typeName} does not match type:${valueType.typeName}")
                            }
                        }
                        MappingData(it.source, it.target, resultTypeHandler)
                    }
                }
            }
            else -> {
                //itemType既不是自定义bean也不是map时只能是单值类型，比如：Integer,LocalDateTime,String
                resultMappings?.apply {
                    value.forEach {
                        val resultTypeHandler = it.mirroredType { typeHandler }
                        if (!resultTypeHandler.isSameType(ResultTypeHandler::class.type)) {
                            val handleMethod = it.mirroredType { typeHandler }.findMethod("handle", Any::class.type)
                                ?: throw HandlerException("Invalid ResultTypeHandler:${resultTypeHandler.typeName}, cannot find method:handle(Object.class)")
                            if (!handleMethod.returnType.isAssignable(itemType)) {
                                throw HandlerException("${resultTypeHandler.typeName} does not match type:${itemType.typeName}")
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