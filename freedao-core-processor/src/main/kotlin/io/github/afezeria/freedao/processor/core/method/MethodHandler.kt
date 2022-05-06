package io.github.afezeria.freedao.processor.core.method

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.spi.BuildMethodService
import io.github.afezeria.freedao.processor.core.spi.BuildService
import io.github.afezeria.freedao.processor.core.spi.MethodFactory
import io.github.afezeria.freedao.processor.core.template.PositionalXMLReader
import io.github.afezeria.freedao.processor.core.template.RootElement
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType

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

    var parameters: MutableList<Parameter> =
        element.parameters.mapIndexedTo(mutableListOf()) { index, variableElement ->
            RealParameter(
                index,
                variableElement.simpleName.toString(),
                variableElement.asType().parameterized(
                    daoHandler.element.asType() as DeclaredType,
                    element.enclosingElement.asType().erasure() as DeclaredType
                ),
                variableElement,
            )
        }

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

    fun render(): MethodSpec {

        ServiceLoader.load(BuildService::class.java, MainProcessor::class.java.classLoader)
            .forEach {
                it.beforeBuildMethod(this)
            }

        //生成方法
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