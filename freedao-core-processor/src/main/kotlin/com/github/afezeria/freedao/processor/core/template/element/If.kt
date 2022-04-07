package com.github.afezeria.freedao.processor.core.template.element

import com.github.afezeria.freedao.processor.core.template.XmlElement

class If : XmlElement() {
    private val test by Attribute()
    override fun render() {
        context.currentScope {
            val flag = context.handleTestExprAndReturnFlagName(test)
            beginControlFlow("if (${flag})")
            super.render()
            endControlFlow()
        }
    }
}