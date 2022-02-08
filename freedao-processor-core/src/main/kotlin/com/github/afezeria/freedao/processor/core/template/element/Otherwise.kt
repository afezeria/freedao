package com.github.afezeria.freedao.processor.core.template.element

import com.github.afezeria.freedao.processor.core.template.XmlElement

class Otherwise : XmlElement() {
    override fun render() {
        if (parent !is Choose) {
            throw RuntimeException("The parent node of the otherwise node must be a choose node")
        }
        if (parent.children.last() != this) {
            throw RuntimeException("Otherwise node must be the last child of the when node")
        }
        val flag = (parent as Choose).flagName
        context.currentScope {
            beginControlFlow("if ($flag)")
            super.render()
            endControlFlow()
        }
    }
}