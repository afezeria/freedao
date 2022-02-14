package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.annotation.Dao
import com.github.afezeria.freedao.processor.core.spi.BuildDaoService
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
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
        val time = measureTimeMillis {
            val annotatedElements = roundEnv.getElementsAnnotatedWith(Dao::class.java)
            if (annotatedElements.isEmpty()) return false
            if (isLombokInvoked) {
                init()
                for (annotatedElement in annotatedElements) {
                    processElement(annotatedElement)
                }
            }
        }
        processingEnvironment.messager.printMessage(Diagnostic.Kind.NOTE, "${time}ms")
        println("====================== $time")
        return false
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        processingEnvironment = processingEnv
    }

    private fun init() {
        buildDaoService = ServiceLoader.load(
            BuildDaoService::class.java,
            MainProcessor::class.java.classLoader
        ).first()
    }

    private fun processElement(element: Element) {
        try {
            if (element is TypeElement && element.kind == ElementKind.INTERFACE && element.enclosingElement.kind == ElementKind.PACKAGE) {
//                DaoModel(element).handle()
                DaoModel(element).render()
            } else {
                processingEnvironment.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "dao must be top level interface",
                    element
                )
                return
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
        get() {
            return try {
                Class.forName("lombok.launch.AnnotationProcessorHider\$AstModificationNotifierData")
                    .getField("lombokInvoked")
                    .getBoolean(null)
            } catch (e: Exception) {
                true
            }
        }

}