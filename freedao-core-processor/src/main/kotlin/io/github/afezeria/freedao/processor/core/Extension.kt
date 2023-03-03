package io.github.afezeria.freedao.processor.core

import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 *
 */
lateinit var processingEnvironment: ProcessingEnvironment
val elementUtils: Elements
    get() = processingEnvironment.elementUtils
val typeUtils: Types
    get() = processingEnvironment.typeUtils


fun String.sqlQuote(): String {
    return "${global.quote}${this}${global.quote}"
}

val groupingRegex = Regex("[a-z]+|[0-9]+|[A-Z][a-z]+|[A-Z]++(?![a-z])|[A-Z]")

fun String.toSnakeCase(): String {
    return groupingRegex.findAll(this).joinToString("_") { it.value.lowercase() }
}

fun <R> runCatchingHandlerExceptionOrThrow(element: Element, block: () -> R): R? {
    try {
        return block()
    } catch (e: Throwable) {
        if (e is HandlerException) {
//            processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, e.message, element)

            processingEnvironment.messager.printMessage(Diagnostic.Kind.ERROR, e.message, e.element ?: element)
            if (global.debug) {
                val stringWriter = StringWriter()
                val printWriter = PrintWriter(stringWriter)
                e.printStackTrace(printWriter)
                processingEnvironment.messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    stringWriter.toString()
                )
            }
        } else {
            throw e
        }
    }
    return null
}