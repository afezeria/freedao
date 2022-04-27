package io.github.afezeria.freedao.processor.core

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
        return mutableSetOf(io.github.afezeria.freedao.annotation.Dao::class.qualifiedName!!)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment,
    ): Boolean {
        if (isLombokInvoked) {
            roundEnv.getElementsAnnotatedWith(io.github.afezeria.freedao.annotation.Dao::class.java).takeIf { it.isNotEmpty() }
                ?.let {
                    val time = measureTimeMillis {
                        it.forEach {
                            processElement(it)
                        }
                    }
//                    processingEnvironment.messager.printMessage(Diagnostic.Kind.NOTE, "freedao processing time:${time}ms")
                    println("========= freedao:${time}ms")
                }
        }
        return false
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        processingEnvironment = processingEnv
        GlobalState.init()
    }

    private fun processElement(element: Element) {
        try {
            runCatchingHandlerExceptionOrThrow(element) {
                DaoHandler(element as TypeElement).render()
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