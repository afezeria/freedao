package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.annotation.Dao
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.system.measureTimeMillis


/**
 *
 */
class MainProcessor : AbstractProcessor() {
    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Dao::class.qualifiedName!!)
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment,
    ): Boolean {
        if (isLombokInvoked) {
            roundEnv.getElementsAnnotatedWith(Dao::class.java).takeIf { it.isNotEmpty() }
                ?.let {
                    val time = measureTimeMillis {
                        it.forEach {
                            processElement(it)
                        }
                    }
                    //NOTE级别要在gradle中开启debug模式才会输出，但是debug模式输出的其他信息太多了，实际上只能用WARNING级别
                    processingEnvironment.messager.printMessage(Diagnostic.Kind.WARNING, "${time}ms")
                    println("====================== $time")
                }
        }
        return false
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        processingEnvironment = processingEnv
    }

    private fun processElement(element: Element) {
        try {
            runCatchingHandlerExceptionOrThrow(element) {
                DaoModel(element as TypeElement).render()
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