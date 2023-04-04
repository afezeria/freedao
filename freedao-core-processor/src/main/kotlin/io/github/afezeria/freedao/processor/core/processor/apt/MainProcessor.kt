package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.annotation.Dao
import io.github.afezeria.freedao.annotation.Table
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.processor.ANY_TYPE
import io.github.afezeria.freedao.processor.core.processor.typeService
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic
import kotlin.reflect.full.memberProperties
import kotlin.system.measureTimeMillis


/**
 *
 */
class MainProcessor : AbstractProcessor() {

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        processingEnvironment = processingEnv
        GlobalState.init()

        typeService = AptTypeService(processingEnv)
        AptLazyType.typeCache = ConcurrentHashMap()
        ANY_TYPE = AptLazyType.valueOf(elementUtils.getTypeElement("java.lang.Object").asType() as DeclaredType)
    }

    fun resetCache() {
        AptLazyType.typeCache = ConcurrentHashMap()
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(
            *GlobalState::class.memberProperties.map {
                "freedao.${it.name}"
            }.toTypedArray()
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Dao::class.qualifiedName!!, Table::class.qualifiedName!!)
    }

    private val elementCache = mutableSetOf<Element>()

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment,
    ): Boolean {
        if (isLombokInvoked) {
            val elements = roundEnv.getElementsAnnotatedWith(Dao::class.java)
            if (elements.isNotEmpty() || elementCache.isNotEmpty()) {
                val interfaceCot = elements.size + elementCache.size
                val time = measureTimeMillis {
                    elements.forEach {
                        it as TypeElement
                        val declaredType = it.asType() as DeclaredType
                        val strMap = typeUtils.getDeclaredType(
                            it,
                            elementUtils.getTypeElement(String::class.java.canonicalName).asType()
                        )

                        val type = AptLazyType.valueOf(it.superclass as DeclaredType)
                        type.typeParameters
                        type.superClass
                        println()
//                        val myMapString = typeUtils.getDeclaredType(
//                            declaredType,
//                            elementUtils.getTypeElement(String::class.java.canonicalName)
//                        )
                        processElement(it)
                    }
                    elementCache.removeAll {
                        processElement(it)
                        true
                    }
                }
                println("========= number of DAO interface: $interfaceCot")
                println("========= freedao total time: ${time}, average time: ${time / interfaceCot}ms")
            }
        } else {
            elementCache.addAll(roundEnv.getElementsAnnotatedWith(Dao::class.java))
        }
        return false
    }


    private fun processElement(element: Element) {
        try {
            runCatchingHandlerExceptionOrThrow(element) {
                element as TypeElement
                DaoHandler(typeService.getByClassName(element.toString())).render()
            }
        } catch (e: Exception) {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            e.printStackTrace(printWriter)
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                stringWriter.toString()
            )
        }
    }

    private val isLombokInvoked: Boolean
        get() = runCatching {
            Class.forName("lombok.launch.AnnotationProcessorHider\$AstModificationNotifierData")
                .getField("lombokInvoked")
                .getBoolean(null)
        }.getOrDefault(true)
}