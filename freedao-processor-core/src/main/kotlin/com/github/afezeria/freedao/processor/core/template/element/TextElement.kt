package com.github.afezeria.freedao.processor.core.template.element

import com.github.afezeria.freedao.processor.core.template.TemplateHandler
import com.github.afezeria.freedao.processor.core.template.XmlElement
import java.util.*

class TextElement : XmlElement() {

    override fun render() {
        val list = mutableListOf<Pair<String?, String?>>()
        val parameterList = mutableListOf<String>()

        val content = xmlNode.textContent
        //将文本按字符串参数分割
        val split = content.split(namedStringParameterRegex).mapTo(LinkedList()) { it }
        val stringParameterList =
            namedStringParameterRegex.findAll(content).mapTo(LinkedList()) { it.groupValues[1] }
        //将每个分组中的sql参数替换成占位符
        split.forEachIndexed { index, s ->
            split[index] = s.replace(namedSqlParameterRegex) {
                parameterList += it.groupValues[1]
                context.placeholderGen.gen()
            }
        }
        while (split.isNotEmpty() || stringParameterList.isNotEmpty()) {
            //纯文本放在first 需要运行时替换的文本放在 second
            if (split.isNotEmpty()) {
                list += split.pop() to null
            }
            if (stringParameterList.isNotEmpty()) {
                list += null to stringParameterList.pop()
            }
        }

        context.currentScope { builderName ->
            var str = ""
            for ((text, variable) in list) {
                if (text != null) {
                    str += text
                } else {
                    variable!!
                    if (str.isNotEmpty()) {
                        addStatement("$builderName.append(\$S)", str)
                        str = ""
                    }
                    val (tmpVar, _) = context.createInternalVariableByContextValue(variable)
                    addStatement("$builderName.append(\$T.toString(${tmpVar}))", Objects::class.java)
                }
            }
            if (str.isNotEmpty()) {
                addStatement("$builderName.append(\$S)", str)
            }
            for (param in parameterList) {
                val (tmpVar, _) = context.createInternalVariableByContextValue(param)
                addStatement("${TemplateHandler.sqlArgsVarName}.add(${tmpVar})")
            }
        }

    }

    companion object {
        private val namedStringParameterRegex = "\\$\\{((?:[a-zA-Z0-1_]+\\.)*[a-zA-Z0-1_]+)}".toRegex()
        private val namedSqlParameterRegex = "#\\{((?:[a-zA-Z0-9_]+\\.)*[a-zA-Z0-9_]+)}".toRegex()
    }
}
