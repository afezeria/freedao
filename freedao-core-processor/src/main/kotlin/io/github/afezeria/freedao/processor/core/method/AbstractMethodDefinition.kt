package io.github.afezeria.freedao.processor.core.method

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.processor.LazyMethod
import io.github.afezeria.freedao.processor.core.processor.LazyParameter
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import io.github.afezeria.freedao.processor.core.processor.typeService
import io.github.afezeria.freedao.processor.core.spi.BuildMethodService
import io.github.afezeria.freedao.processor.core.spi.BuildService
import io.github.afezeria.freedao.processor.core.spi.MethodFactory
import io.github.afezeria.freedao.processor.core.template.PositionalXMLReader
import io.github.afezeria.freedao.processor.core.template.RootElement
import org.w3c.dom.Document
import java.io.ByteArrayInputStream
import java.util.*

/**
 *
 */
abstract class AbstractMethodDefinition protected constructor(
    val daoHandler: DaoHandler,
    val method: LazyMethod,
) : MethodDefinition, ResultMappingInfo by ResultMappingInfo(method) {
    val qualifiedName = method.qualifiedName

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
     * 方法是否返回受影响行数
     */
    var returnUpdateCount = false
//    var resultHelper: ResultHelper = ResultHelper(daoHandler, element)


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
    }

    fun initReturnTypeInfo() {

    }

    override fun render() {

        ServiceLoader.load(BuildService::class.java, MainProcessor::class.java.classLoader)
            .forEach {
                it.beforeBuildMethod(this)
            }

        //生成方法
        builder = typeService.createMethodSpecBuilder(method)

        //process non-null parameter
        parameters.forEach {
            if (it.annotationNames.any { ann -> ann in notNullAnnotationSet }) {
                builder.addStatement("\$T.requireNonNull(${it.simpleName})", Objects::class.java)
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

        operator fun invoke(daoHandler: DaoHandler, method: LazyMethod): AbstractMethodDefinition {
            return XmlTemplateMethod(daoHandler, method)
                ?: CrudMethod(daoHandler, method)
                ?: NamedMethod(daoHandler, method)
                ?: methodFactories.firstNotNullOfOrNull { it.create(daoHandler, method) }
                ?: throw HandlerException("Invalid method declare")
        }
    }

}