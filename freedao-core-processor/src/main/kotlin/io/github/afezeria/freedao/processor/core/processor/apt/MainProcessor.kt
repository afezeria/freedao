package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.annotation.Dao
import io.github.afezeria.freedao.annotation.Table
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.GlobalState
import io.github.afezeria.freedao.processor.core.processingEnvironment
import io.github.afezeria.freedao.processor.core.processor.typeService
import io.github.afezeria.freedao.processor.core.runCatchingHandlerExceptionOrThrow
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.full.memberProperties
import kotlin.system.measureTimeMillis


/**
 *
 */
class MainProcessor : AbstractProcessor() {
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

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        processingEnvironment = processingEnv
        GlobalState.init()
        typeService = AptTypeService(processingEnv)
    }


    private fun processElement(element: Element) {
        try {
            println()
//            typeService.get(ArrayList::class.javaObjectType.canonicalName!!)
            runCatchingHandlerExceptionOrThrow(element) {
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