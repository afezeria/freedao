package io.github.afezeria.freedao.processor.core.template.element

import io.github.afezeria.freedao.processor.core.template.PositionalXMLReader
import org.w3c.dom.Node
import java.lang.reflect.Proxy
import java.util.*

class In : Foreach() {
    private val randomValue = "item" + Random().nextInt(Int.MAX_VALUE).toString()

    init {
        validatorMap["item"] = { it ?: randomValue }
        validatorMap["open"] = { "(" }
        validatorMap["close"] = { ")" }
        validatorMap["separator"] = { "," }
    }

    override fun render() {
        val text = if (item != randomValue) {
            "#{$randomValue.$item}"
        } else {
            "#{$item}"
        }
        item = randomValue
        children.clear()
        children.add(TextElement().apply {
            parent = this@In
            xmlNode = Proxy.newProxyInstance(this.javaClass.classLoader, arrayOf(Node::class.java)) { _, method, args ->
                if (method.name == Node::getTextContent.name) {
                    text
                } else if (method.name == Node::getUserData.name
                    && (args.contentEquals(arrayOf(PositionalXMLReader.LINE_NUMBER_KEY_NAME))
                            || args.contentEquals(arrayOf(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME)))
                ) {
                    this@In.xmlNode.getUserData(args[0].toString())
                } else {
                    throw NotImplementedError()
                }
            } as Node
        })
        super.render()
    }

}