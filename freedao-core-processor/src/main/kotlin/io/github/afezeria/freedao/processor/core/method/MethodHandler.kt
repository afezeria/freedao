package io.github.afezeria.freedao.processor.core.method

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import io.github.afezeria.freedao.processor.core.spi.BuildMethodService
import io.github.afezeria.freedao.processor.core.spi.BuildService
import io.github.afezeria.freedao.processor.core.spi.MethodFactory
import io.github.afezeria.freedao.processor.core.template.PositionalXMLReader
import io.github.afezeria.freedao.processor.core.template.RootElement
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.util.*
import javax.lang.model.element.ExecutableElement

/**
 *
 */
abstract class MethodHandler protected constructor(
    val element: ExecutableElement,
    val daoHandler: DaoHandler,
    val method: LazyMethod,
) : MethodDefinition {
    val name = method.name

    //    init{
//    }
//    val name: String = element.simpleName.toString()
    var parameters: MutableList<LazyParameter> = method.parameters.toMutableList()

    //    var parameters: MutableList<Parameter> =
//        element.parameters.mapIndexedTo(mutableListOf()) { index, variableElement ->
//            RealParameter(
//                index,
//                variableElement.simpleName.toString(),
//                variableElement.asType().parameterized(
//                    daoHandler.element.asType() as DeclaredType,
//                    element.enclosingElement.asType().erasure() as DeclaredType
//                ),
//                variableElement,
//            )
//        }
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
    var returnTypeContainerType: LazyType? = null

    /**
     * 当返回类型为Map或Map的集合时，值为Map的值的类型
     * 否则为null
     */
    var returnTypeMapValueType: LazyType? = null

    /**
     * 当方法返回多行结果集时，返回类型为容器类型，该值为集合元素的类型
     *
     * 否则该值为返回结果的类型
     *
     * 当类型为Map的接口时值为HashMap，当类型为原始类型时值为原始类型的包装类
     *
     * 该值的类型不会是抽象的
     */
    var returnTypeItemType: LazyType

    /**
     * 同[itemType]，但是当类型为Map时，值为实际声明的类型，可能为抽象类或接口
     */
    var returnTypeOriginalItemType: LazyType

    var returnUpdateCount = false
    var resultHelper: ResultHelper = ResultHelper(daoHandler, element)
    val mappings = ResultMappingsAnn.getMappings(element, resultHelper)
    val xmlDocument: Document by lazy {
        PositionalXMLReader.readXML(ByteArrayInputStream(getTemplate().toByteArray()))
    }
    val sqlBuildCodeBlock: CodeBlock by lazy {
        RootElement(this).buildCodeBlock()
    }
    val statementType: StatementType by lazy {
        try {
            StatementType.valueOf(xmlDocument.firstChild.nodeName.uppercase())
        } catch (e: Exception) {
            throw HandlerException("unknown statement type")
        }
    }
    lateinit var builder: MethodSpec.Builder

    init {
        if (method.typeParameters.isNotEmpty()) {
            throw HandlerException("Method cannot have TypeParameter")
        }
        val returnType = method.returnType
        when (returnType) {
            is NoType -> {
                throw HandlerException("Invalid return type, cannot return void")
            }
            is PrimitiveType -> {
                returnTypeItemType = returnType.boxed()
                returnTypeOriginalItemType = returnTypeItemType
            }
            else -> {
                //检查类型
                if (returnType is CollectionType) {
                    //多行返回值
                    returnTypeContainerType = if (returnType.isAbstract) {
                        when {
                            returnType.isSameType(List::class) -> ArrayList::class
                            returnType.isSameType(List::class) -> HashSet::class
                            returnType.isSameType(List::class) -> ArrayList::class
                            else -> throw HandlerException("Invalid return type")
                        }.lazyType
                    } else {
                        returnType.erasure()
                    }

                    returnTypeOriginalItemType = returnType.elementType
                } else {
                    //单行返回值
                    returnTypeOriginalItemType = returnType
                }
                //结果集行类型为抽象类型时类型必须为Map
                if (returnTypeOriginalItemType.isAbstract && !returnTypeOriginalItemType.erasure()
                        .isSameType(Map::class)
                ) {
                    throw HandlerException("Invalid return type:$returnTypeOriginalItemType, the abstract type of single row result can only be Map")
                }
                returnTypeItemType = when (returnTypeOriginalItemType) {
                    is CollectionType -> {
                        throw HandlerException("Invalid type argument:$returnTypeOriginalItemType")
                    }
                    is MapType -> {
                        val mapType = returnTypeOriginalItemType as MapType
                        if (!mapType.keyType.isSameType(String::class)) {
                            throw HandlerException("Invalid type argument:${mapType.keyType}, the key type must be String")
                        }
                        returnTypeMapValueType = mapType.valueType.apply {
                            if (isAbstract) {
                                throw HandlerException("Invalid type argument:$this, the value type cannot be abstract")
                            }
                        }
                        if (mapType.isAbstract) {
                            HashMap::class.lazyType
                        } else {
                            mapType
                        }
                    }
                    else -> {
                        returnTypeOriginalItemType
                    }
                }.erasure()
            }
        }
    }

    override fun render() {

        ServiceLoader.load(BuildService::class.java, MainProcessor::class.java.classLoader)
            .forEach {
                it.beforeBuildMethod(this)
            }

        builder = method.buildMethodSpec()

        //生成方法
        builder = method.buildMethodSpec()
        builder = MethodSpec.overriding(element).apply {
            returns(resultHelper.returnType.typeName)
            parameters.clear()
            addParameters(
                this@MethodHandler.parameters
                    .filterIsInstance<RealParameter>()
                    .map {
                        ParameterSpec.builder(
                            it.type.typeName,
                            it.name,
                            *it.variableElement.modifiers.toTypedArray()
                        ).addAnnotations(
                            it.variableElement.annotationMirrors.map { ann -> AnnotationSpec.get(ann) }
                        ).build()
                    }
            )
            addAnnotations(
                element.annotationMirrors.map {
                    AnnotationSpec.get(it)
                }
            )
        }
        //process non-null parameter
        for (parameter in parameters) {
            parameter.annotations
        }
        element.parameters.forEach { param ->
            param.annotationMirrors
                .find { it.annotationType.asElement().simpleName.toString() in notNullAnnotationSet }
                ?.let {
                    builder.addStatement("\$T.requireNonNull(${param.simpleName})", Objects::class.type)
                }
        }

        val codeBlock = buildMethodService.build(this)
        builder.addCode(codeBlock)

        daoHandler.classBuilder.addMethod(builder.build())
    }

    abstract fun getTemplate(): String

    companion object {
        private val notNullAnnotationSet = setOf("NotNull", "NonNull")

        private val buildMethodService by lazy {
            ServiceLoader.load(
                BuildMethodService::class.java,
                MainProcessor::class.java.classLoader
            ).toList().run {
                require(size == 1)
                get(0)
            }
        }

        private val methodFactories by lazy {
            ServiceLoader.load(
                MethodFactory::class.java,
                MainProcessor::class.java.classLoader
            ).sortedBy { it.order() }
        }

        operator fun invoke(element: ExecutableElement, daoHandler: DaoHandler): MethodHandler {
            return XmlTemplateMethod(element, daoHandler)
                ?: CrudMethod(element, daoHandler)
                ?: NamedMethod(element, daoHandler)
                ?: methodFactories.firstNotNullOfOrNull { it.create(element, daoHandler) }
                ?: throw HandlerException("Invalid method declare")
        }
    }

}