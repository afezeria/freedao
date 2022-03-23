package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.*
import com.github.afezeria.freedao.processor.core.spi.BuildMethodService
import com.github.afezeria.freedao.processor.core.spi.MethodFactory
import com.github.afezeria.freedao.processor.core.spi.ValidatorService
import com.github.afezeria.freedao.processor.core.template.PositionalXMLReader
import com.github.afezeria.freedao.processor.core.template.RootElement
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

/**
 *
 */
abstract class MethodHandler protected constructor(
    val element: ExecutableElement,
    val daoHandler: DaoHandler,
) {
    val name: String = element.simpleName.toString()

    init {
        if (element.typeParameters.isNotEmpty()) {
            throw HandlerException("Method cannot have TypeParameter")
        }
    }

    var parameters: List<Parameter> =
        element.parameters.mapIndexed { index, variableElement ->
            Parameter(
                index,
                variableElement.simpleName.toString(),
                variableElement.asType().parameterized(
                    daoHandler.element.asType() as DeclaredType,
                    element.enclosingElement.asType().erasure() as DeclaredType
                ),
                variableElement,
            )
        }
    val requiredParameters = mutableListOf<Parameter>()

    var returnUpdateCount = false

    var resultHelper: ResultHelper = ResultHelper(daoHandler, element)
    val mappings = ResultMappingsAnn.getMappings(this)

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

    fun render(): MethodSpec {

        ServiceLoader.load(ValidatorService::class.java, MainProcessor::class.java.classLoader)
            .forEach {
                it.validation(this)
            }

        //生成方法
        builder = MethodSpec.overriding(element).apply {
            returns(resultHelper.returnType.typeName)
            parameters.clear()
            addParameters(
                this@MethodHandler.parameters.map {
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
        element.parameters.forEach { param ->
            param.annotationMirrors
                .find { it.annotationType.asElement().simpleName.toString() in notNullAnnotationSet }
                ?.let {
                    builder.addStatement("\$T.requireNonNull(${param.simpleName})", Objects::class.type)
                }
        }

        val codeBlock = buildMethodService.build(this)
        builder.addCode(codeBlock)

        return builder.build()
    }


    abstract fun getTemplate(): String


    fun requireParameterByTypes(vararg types: TypeMirror) {
        var matched = false
        var idx = 0
        types.forEach { type ->
            while (idx < parameters.size) {
                if (parameters[idx].type.isAssignable(type)) {
                    requiredParameters += parameters[idx]
                    matched = true
                    idx++
                    return@forEach
                } else {
                    if (matched) {
                        break
                    }
                    idx++
                }
            }
            throw HandlerException("Missing parameter of type ${types[0]}")
        }
    }

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
                ?: AnnotationStyleMethod(element, daoHandler)
                ?: CrudMethod(element, daoHandler)
                ?: NamedMethod(element, daoHandler)
                ?: methodFactories.firstNotNullOfOrNull { it.create(element, daoHandler) }
                ?: throw HandlerException("Invalid method declare")
        }
    }

}