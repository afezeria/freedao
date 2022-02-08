package com.github.afezeria.freedao.processor.core.template

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.method.MethodModel
import com.squareup.javapoet.CodeBlock
import org.w3c.dom.Document
import java.io.ByteArrayInputStream

class RootElement(methodModel: MethodModel) : XmlElement() {

    lateinit var document: Document

    override val context: TemplateHandler
    val template: String

    init {
        template = methodModel.getTemplate()
        context =
            TemplateHandler(
                methodModel.element.parameters
                    .associate { it.simpleName.toString() to it.asType() }
            )
        val document = PositionalXMLReader.readXML(ByteArrayInputStream(template.toByteArray()))
//        val document = documentBuilder.parse(ByteArrayInputStream(template.toByteArray()))
        try {
            methodModel.statementType =
                StatementType.valueOf(document.firstChild.nodeName.uppercase())
        } catch (e: Exception) {
            throw HandlerException("unknown statement type")
        }

        init(document.childNodes.item(0))
        println()
    }

    fun buildCodeBlock(): CodeBlock {
        render()
        return context.handle()
    }

//    companion object {
//        private val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
//    }
}