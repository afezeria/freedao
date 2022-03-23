package com.github.afezeria.freedao.processor.core.template

import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.squareup.javapoet.CodeBlock

class RootElement(methodHandler: MethodHandler) : XmlElement() {

    override val context: TemplateHandler

    init {
        context =
            TemplateHandler(
                methodHandler.parameters
                    .associate { it.name to it.type}
            )

        init(methodHandler.xmlDocument.childNodes.item(0))
    }

    fun buildCodeBlock(): CodeBlock {
        render()
        return context.handle()
    }

}