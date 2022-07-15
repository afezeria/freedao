package io.github.afezeria.freedao.processor.core.template

import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import io.github.afezeria.freedao.processor.core.template.PositionalXMLReader.COLUMN_NUMBER_KEY_NAME
import io.github.afezeria.freedao.processor.core.template.PositionalXMLReader.LINE_NUMBER_KEY_NAME
import io.github.afezeria.freedao.processor.core.template.element.TextElement
import org.w3c.dom.Node
import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.primaryConstructor

abstract class XmlElement {

    protected open val context: TemplateHandler
        get() {
            return parent.context
        }

    protected val validatorMap = mutableMapOf<String, (String?) -> String>()
    protected val attributeMap = mutableMapOf<String, String>()
    val children: MutableList<XmlElement> = mutableListOf()
    lateinit var parent: XmlElement
    lateinit var xmlNode: Node
    val lineNumber: Int
        get() = xmlNode.getUserData(LINE_NUMBER_KEY_NAME) as Int
    val columnNumber: Int
        get() = xmlNode.getUserData(COLUMN_NUMBER_KEY_NAME) as Int

    protected fun init(node: Node) {
        xmlNode = node
        node.apply {
            //节点类型为text时attributes为null
            //忽略idea警告，这里的attributes可能为null
            //不能用hasAttributes()判断，当attributes不为null但大小为0时，hashAttributes()会返回false
            attributes?.apply {
                for (i in 0 until length) {
                    attributeMap[item(i).nodeName] = item(i).nodeValue
                }
                //检查XmlElement子类中用Attribute声明的代理属性是否为空并赋值
                for ((name, validator) in validatorMap) {
                    attributeMap[name] = validator(attributeMap[name])
                }
            }

            childNodes.takeIf { hasChildNodes() }?.apply {
                (0 until length).forEach { idx ->
                    item(idx).let { child ->
                        createElement(child).let {
                            it.parent = this@XmlElement
                            it.init(child)
                            children += it
                        }
                    }
                }
            }
        }
    }

    open fun render() {
        context.currentScope {
            add("//${xmlNode.nodeName} ${
                xmlNode.attributes.run {
                    (0 until length).joinToString {
                        "${item(it).nodeName}=\"${
                            item(it).nodeValue
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                        }\""
                    }
                }
            }\n")
        }
        children.forEach { it.render() }
    }

    private fun createElement(node: Node): XmlElement {
        return when (node.nodeType) {
            //common text node
            3.toShort() -> TextElement()
            //element node
            1.toShort() -> elementBuilderMap[node.nodeName.replaceFirstChar { it.uppercaseChar() }]?.call()
                ?: throw HandlerException("Invalid node:${node.nodeName}")
            //现在用SAX解析xml暂时没遇到那种情况会走到这
            else -> throw IllegalStateException("unreachable")
        }
    }

    fun throwWithPosition(msg: String): Nothing {
        throw HandlerException(
            "[line:${lineNumber}, column:${columnNumber}, element:${xmlNode.nodeName}] $msg"
        )
    }

    /**
     * 节点属性
     * @property default String? 默认值，为null时表示该属性必填
     * @constructor
     */
    inner class Attribute(
        private val default: String? = null,
    ) {
        operator fun provideDelegate(
            thisRef: XmlElement,
            prop: KProperty<*>,
        ): ReadWriteProperty<XmlElement, String> {
            thisRef.validatorMap[prop.name] = { v ->
                v ?: default
                ?: throwWithPosition("missing required attribute:${prop.name}")
            }
            return object : ReadWriteProperty<XmlElement, String> {
                override fun getValue(thisRef: XmlElement, property: KProperty<*>): String {
                    return attributeMap[property.name]!!
                }

                override fun setValue(thisRef: XmlElement, property: KProperty<*>, value: String) {
                    attributeMap[property.name] = value
                }

            }
        }
    }

    companion object {
        private val elementBuilderMap: Map<String, KFunction<XmlElement>> by lazy {
            ServiceLoader.load(XmlElement::class.java, MainProcessor::class.java.classLoader)
                .associate { it.javaClass.simpleName to it.javaClass.kotlin.primaryConstructor!! }
        }
    }
}

