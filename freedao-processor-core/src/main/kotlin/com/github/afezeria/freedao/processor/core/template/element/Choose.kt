package com.github.afezeria.freedao.processor.core.template.element

import com.github.afezeria.freedao.processor.core.template.XmlElement
import com.github.afezeria.freedao.processor.core.type

class Choose : XmlElement() {
    lateinit var flagName: String
    override fun render() {
        if (children.any { it !is When && it !is Otherwise }) {
            throw RuntimeException("The child node of choose node can only be when node or otherwise node")
        }
        flagName = context.createInternalFlag(Boolean::class.type, true)
        super.render()
    }
}