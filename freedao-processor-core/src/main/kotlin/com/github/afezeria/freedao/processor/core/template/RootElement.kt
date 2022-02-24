package com.github.afezeria.freedao.processor.core.template

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.squareup.javapoet.CodeBlock
import org.w3c.dom.Document
import java.io.ByteArrayInputStream

class RootElement(methodHandler: MethodHandler) : XmlElement() {

    val document: Document

    override val context: TemplateHandler
    val template: String

    init {
        template = methodHandler.getTemplate()
        context =
            TemplateHandler(
                methodHandler.parameters
                    .associate { it.name to it.type}
            )
        document = PositionalXMLReader.readXML(ByteArrayInputStream(template.toByteArray()))
        try {
            methodHandler.statementType =
                StatementType.valueOf(document.firstChild.nodeName.uppercase())
        } catch (e: Exception) {
            throw HandlerException("unknown statement type")
        }

        init(document.childNodes.item(0))
    }

    fun buildCodeBlock(): CodeBlock {
        render()
        return context.handle()
    }

}