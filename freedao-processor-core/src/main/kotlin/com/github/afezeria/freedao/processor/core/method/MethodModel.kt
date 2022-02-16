package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.*
import com.github.afezeria.freedao.processor.core.spi.BuildMethodService
import com.github.afezeria.freedao.processor.core.spi.MethodFactory
import com.github.afezeria.freedao.processor.core.spi.ValidatorService
import com.github.afezeria.freedao.processor.core.template.RootElement
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror

/**
 *
 */
abstract class MethodModel protected constructor(
    val element: ExecutableElement,
    val daoModel: DaoModel,
) {
    val name: String = element.simpleName.toString()

    init {
        if (element.typeParameters.isNotEmpty()) {
            throw HandlerException("method cannot have TypeParameter")
        }
    }
    var parameters: List<Parameter> =
        element.parameters.mapIndexed { index, variableElement ->
            Parameter(
                index,
                variableElement.simpleName.toString(),
                ObjectModel(
                    variableElement.asType().parameterized(
                        daoModel.element.asType() as DeclaredType,
                        element.enclosingElement.asType().erasure() as DeclaredType
                    )
                ),
                variableElement,
            )
        }
    var returnUpdateCount = false
    lateinit var statementType: StatementType

    var resultHelper: ResultHelper = ResultHelper(daoModel, element)

    lateinit var sqlBuildCodeBlock: CodeBlock
    lateinit var builder: MethodSpec.Builder
    var methodSpec: MethodSpec? = null

    fun render(): MethodSpec {
        //generate build sql code
        sqlBuildCodeBlock = RootElement(this).buildCodeBlock()
        if (statementType == StatementType.SELECT && resultHelper.returnType is PrimitiveType) {
            throw HandlerException("select method cannot return primitive type")
        }

        ServiceLoader.load(ValidatorService::class.java, MainProcessor::class.java.classLoader)
            .forEach {
                it.validation(this)
            }

        builder = MethodSpec.overriding(element).apply {
            returns(resultHelper.returnType.typeName)
            parameters.clear()
            addParameters(
                this@MethodModel.parameters.map {
                    ParameterSpec.builder(
                        it.model.typeMirror.typeName,
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

    fun requireParameter(typeMirror: TypeMirror, name: String? = null) {
        if (name == null) {
            when (
                parameters.filter {
                    typeUtils.isAssignable(it.model.typeMirror, typeMirror)
                }.size
            ) {
                0 -> throw HandlerException("Missing parameter of type ${typeMirror.typeName}")
                //valid
                1 -> {}
                else ->
                    throw HandlerException("Duplicate ${typeMirror.typeName} type parameter")
            }
        } else {
            if (parameters.none { it.name == name && typeUtils.isAssignable(it.model.typeMirror, typeMirror) }) {
                val parameterDeclare =
                    if (daoModel.isJavaCode) "${typeMirror.typeName} $name"
                    else "$name: ${typeMirror.typeName}"
                throw HandlerException("missing parameter [$parameterDeclare]")
            }
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

        fun BeanObjectModel.getProperties(): List<BeanProperty> {
            return element.enclosedElements.filter {
                it.hasGetter()
            }.map { BeanProperty(it as VariableElement) }
        }

        private val methodFactories by lazy {
            ServiceLoader.load(
                MethodFactory::class.java,
                MainProcessor::class.java.classLoader
            ).sortedBy { it.order() }
        }

        operator fun invoke(element: ExecutableElement, daoModel: DaoModel): MethodModel {
            return XmlTemplateMethod(element, daoModel)
                ?: AnnotationStyleMethod(element, daoModel)
                ?: CrudMethod(element, daoModel)
                ?: NamedMethod(element, daoModel)
                ?: methodFactories.firstNotNullOfOrNull { it.create(element, daoModel) }
                ?: throw HandlerException("invalid method declare")
        }
    }

}