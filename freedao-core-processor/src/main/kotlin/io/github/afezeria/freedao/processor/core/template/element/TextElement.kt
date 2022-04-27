package io.github.afezeria.freedao.processor.core.template.element

import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.elementUtils
import io.github.afezeria.freedao.processor.core.isParameterTypeHandlerAndMatchType
import io.github.afezeria.freedao.processor.core.isSameType
import io.github.afezeria.freedao.processor.core.template.TemplateHandler
import io.github.afezeria.freedao.processor.core.template.XmlElement
import java.util.*

class TextElement : XmlElement() {
    /**
     * xml节点文本内容按字符串参数分割后的片段
     * @property isVar Boolean true:模板变量,false:纯文本
     * @property str String
     * @constructor
     */
    private data class TextFragment(val isVar: Boolean, val str: String)

    override fun render() {

        val content = xmlNode.textContent
        //将文本按字符串参数分割
        val split = content.split(namedStringParameterRegex).mapTo(LinkedList()) { it }
        val stringParameterList = namedStringParameterRegex.findAll(content).mapTo(LinkedList()) { it.groupValues[1] }

        val list = mutableListOf<TextFragment>()
        while (split.isNotEmpty() || stringParameterList.isNotEmpty()) {
            if (split.isNotEmpty()) {
                list += TextFragment(false, split.pop())
            }
            if (stringParameterList.isNotEmpty()) {
                list += TextFragment(true, stringParameterList.pop())
            }
        }

        context.currentScope { builderName ->
            var str = ""
            for ((isVar, text) in list) {
                if (isVar) {
                    //字符串替换
                    if (str.isNotEmpty()) {
                        addStatement("$builderName.append(\$S)", str)
                        str = ""
                    }
                    val (tmpVar, _) = context.createInternalVariableByContextValue(text)
                    addStatement("$builderName.append(\$T.toString(${tmpVar}))", Objects::class.java)
                } else {
                    str += text.replace(namedSqlParameterRegex) {
                        //将字符串中的sql参数替换为占位符
                        val (tmpVar, exprType) = context.createInternalVariableByContextValue(it.groupValues[1])
                        val pair =
                            it.groupValues[2].takeIf { it.isNotBlank() }
                                ?.run {
                                    (elementUtils.getTypeElement(this)
                                        ?: throw HandlerException("class not found:$this")).asType()
                                        .isParameterTypeHandlerAndMatchType(exprType)
                                }
                        if (pair == null || pair.first.isSameType(io.github.afezeria.freedao.ParameterTypeHandler::class)) {
                            addStatement("${TemplateHandler.sqlArgsVarName}.add(${tmpVar})")
                        } else {
                            val (handlerType, handleMethodParameterType) = pair
                            if (exprType.isSameType(Any::class) && !handleMethodParameterType.isSameType(Any::class)) {
                                addStatement("${TemplateHandler.sqlArgsVarName}.add($handlerType.handleParameter(($handleMethodParameterType) ${tmpVar}))")
                            } else {
                                addStatement("${TemplateHandler.sqlArgsVarName}.add($handlerType.handleParameter(${tmpVar}))")
                            }
                        }
                        context.placeholderGen.gen()
                    }
                }
            }

            if (str.isNotEmpty()) {
                addStatement("$builderName.append(\$S)", str)
            }
        }
    }

    companion object {
        val namedStringParameterRegex = "\\$\\{([a-zA-Z0-9_]+(?:\\.\"[a-zA-Z0-9_]+\"|\\.[a-zA-Z0-9_]+)*)}".toRegex()

        val namedSqlParameterRegex =
            "#\\{([a-zA-Z0-9_]+(?:\\.\"[a-zA-Z0-9_]+\"|\\.[a-zA-Z0-9_]+)*)(?:,typeHandler=(.*?))?}".toRegex()
    }
}
