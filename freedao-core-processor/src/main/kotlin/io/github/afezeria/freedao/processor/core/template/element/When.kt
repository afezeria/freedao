package io.github.afezeria.freedao.processor.core.template.element

import io.github.afezeria.freedao.processor.core.template.XmlElement

class When : XmlElement() {
    private val test by Attribute()

    override fun render() {
        if (parent !is Choose) {
            throwWithPosition("The parent node of the when node must be a choose node")
        }
        val chooseFlag = (parent as Choose).flagName
        context.currentScope {
            val flag = context.handleTestExprAndReturnFlagName(test)
            beginControlFlow("if (${chooseFlag} && ${flag})")
            addStatement("$chooseFlag = \$L", false)
            super.render()
            endControlFlow()
        }

    }
}