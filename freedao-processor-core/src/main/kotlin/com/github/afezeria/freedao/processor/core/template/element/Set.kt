package com.github.afezeria.freedao.processor.core.template.element

class Set : Trim() {
    init {
        validatorMap["prefix"] = { "set " }
        validatorMap["prefixOverrides"] = { "" }
        validatorMap["postfixOverrides"] = { "," }
    }
}