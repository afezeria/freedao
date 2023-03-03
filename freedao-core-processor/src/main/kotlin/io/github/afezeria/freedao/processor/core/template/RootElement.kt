package io.github.afezeria.freedao.processor.core.template

import com.squareup.javapoet.CodeBlock
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition

class RootElement(methodHandler: AbstractMethodDefinition) : XmlElement() {

    override val context: TemplateHandler

    init {
        context =
            TemplateHandler(
                methodHandler.parameters
                    .associate { it.simpleName to it.type }
            )

        init(methodHandler.xmlDocument.childNodes.item(0))
    }

    fun buildCodeBlock(): CodeBlock {
        render()
        return context.handle()
    }

}