package com.github.afezeria.freedao.processor.core.template.element

import com.github.afezeria.freedao.processor.core.template.XmlElement
import com.github.afezeria.freedao.processor.core.type

class Choose : XmlElement() {
    lateinit var flagName: String
    override fun render() {
        children.removeIf {
            it is TextElement
                    && it.xmlNode.textContent.isBlank()
        }
        if (children.any { it !is When && it !is Otherwise }) {
            throwWithPosition("The child node of choose node can only be when node or otherwise node")
        }
        flagName = context.createInternalFlag(Boolean::class.type, true)
        super.render()
    }
}