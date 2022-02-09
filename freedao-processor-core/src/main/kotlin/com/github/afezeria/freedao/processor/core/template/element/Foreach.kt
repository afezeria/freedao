package com.github.afezeria.freedao.processor.core.template.element

import com.github.afezeria.freedao.processor.core.template.XmlElement
import com.github.afezeria.freedao.processor.core.type
import javax.lang.model.type.DeclaredType

class Foreach : XmlElement() {
    private val collection by Attribute()
    private val item by Attribute()
    private val index by Attribute("")
    private val open by Attribute("")
    private val close by Attribute("")
    private val separator by Attribute("")

    /**
     * 转换结果示例
     *
     *     Iterator<String> iterator = strings.iterator();
     *     int index = 0;
     *     while (iterator.hasNext()) {
     *         String a = iterator.next();
     *         if (index > 0) {
     *            stringBuilder.append(",");
     *         }
     *         /*子节点内容*/
     *         index++;
     *     }
     */
    override fun render() {
        context.currentScope { builderName ->
            addStatement("$builderName.append(\$S)", open)
            val (iterableVar, iterableType) = context.createInternalVariableByContextValue(collection,
                Iterable::class.type)
            val indexVar = if (index.isNotEmpty()) {
                context.createTemplateVariable(index, Int::class.type, 0)
            } else {
                context.createInternalFlag(Int::class.type, 0)
            }
            val loopVar = context.createTemplateVariable(item,
                (iterableType as DeclaredType).typeArguments[0],
                null)

            addStatement("$builderName.append(\$S)", open)
            val iteratorVar = context.createInternalFlag(Iterator::class.type(String::class.type), null)
            addStatement("$iteratorVar = $iterableVar.iterator()")
            beginControlFlow("while($iteratorVar.hasNext())")
            children.forEach {
                addStatement("$loopVar = $iteratorVar.next()")
                if (separator.isNotEmpty()) {
                    beginControlFlow("if ($indexVar > 0)")
                    addStatement("$builderName.append(\$S)", separator)
                    endControlFlow()
                }
                it.render()
                addStatement("$indexVar++")
            }
            endControlFlow()
            addStatement("$builderName.append(\$S)", close)

        }
    }
}