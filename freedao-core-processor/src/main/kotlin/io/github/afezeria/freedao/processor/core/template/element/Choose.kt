package io.github.afezeria.freedao.processor.core.template.element

import io.github.afezeria.freedao.processor.core.processor.typeLA
import io.github.afezeria.freedao.processor.core.template.XmlElement

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
        flagName = context.createInternalFlag(Boolean::class.typeLA, true)
        super.render()
    }
}