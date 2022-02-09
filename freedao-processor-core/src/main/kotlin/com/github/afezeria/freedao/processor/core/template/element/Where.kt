package com.github.afezeria.freedao.processor.core.template.element

class Where : Trim() {
    init {
        validatorMap["prefix"] = { "where " }
        validatorMap["prefixOverrides"] = { "and |or " }
        validatorMap["postfixOverrides"] = { "" }
    }
}