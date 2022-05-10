package io.github.afezeria.freedao.processor.core.template.element

import io.github.afezeria.freedao.processor.core.template.TemplateHandler
import io.github.afezeria.freedao.processor.core.template.XmlElement
import io.github.afezeria.freedao.processor.core.type


/**
 * 允许在context中直接编写java代码，要求java代码必须是一个语句，该语句的返回类型必须是字符串
 *
 * 插值语法使用${}格式，编译时会将存储表达式值的变量的变量名添加到指定位置
 */
class Java : XmlElement() {

    override fun render() {
        context.currentScope { builderName ->
            val statement = xmlNode.textContent.replace(TextElement.namedStringParameterRegex) {
                val (tmpVar, exprType) = context.createInternalVariableByContextValue(it.groupValues[1])
                tmpVar
            }
            val variable = context.createTemplateVariable(
                "java_${lineNumber}_${columnNumber}",
                Any::class.type,
                null
            )
            addStatement("$variable = $statement")
            addStatement("$builderName.append(\$S)", "?")
            addStatement("${TemplateHandler.sqlArgsVarName}.add($variable)")
        }
    }
}