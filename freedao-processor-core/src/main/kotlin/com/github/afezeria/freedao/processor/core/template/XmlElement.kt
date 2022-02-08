package com.github.afezeria.freedao.processor.core.template

import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.MainProcessor
import com.github.afezeria.freedao.processor.core.template.element.TextElement
import org.w3c.dom.Node
import java.util.*
import kotlin.properties.ReadOnlyProperty
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

    protected fun init(node: Node) {
        xmlNode = node
        node.apply {
            //节点类型为text时attributes为null
            attributes?.apply {
                for ((name, validator) in validatorMap) {
                    attributeMap[name] = validator(getNamedItem(name)?.nodeValue)
                }
                (0 until length).forEach {
                    item(it)?.takeIf { validatorMap.containsKey(it.nodeName).not() }
                        ?.let { attributeMap[it.nodeName] = it.nodeValue }
                }
            }

            childNodes.takeIf { hasChildNodes() }?.apply {
                (0 until length).forEach { idx ->
                    item(idx).let { child ->
                        createElement(child)?.let {
                            if (it is TextElement && children.isNotEmpty() && children.last() is TextElement) {
                                (children.last() as TextElement).append(it)
                            } else {
                                it.parent = this@XmlElement
                                it.init(child)
                                children += it
                            }
                        }
                    }
                }
            }
        }
    }

    open fun render() {
        context.currentScope {
            xmlNode.attributes.run {
                (0 until length).joinToString {
                    "${item(it).nodeName}=${item(it).nodeValue}"
                }
            }
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

    private fun createElement(node: Node): XmlElement? {
        return when (node.nodeType) {
            3.toShort() -> TextElement(false, node.textContent)
            //cdata
            4.toShort() -> TextElement(true, node.textContent)
            1.toShort() -> requireNotNull(elementBuilderMap[node.nodeName.replaceFirstChar { it.uppercaseChar() }]) {
                "invalid node:${node.nodeName}"
            }.call()
            else -> null
        }
    }

    /**
     * 节点属性
     * @property default String? 默认值，为null时表示该属性必填
     * @constructor
     */
    class Attribute(
        private val default: String? = null,
    ) {
        operator fun provideDelegate(
            thisRef: XmlElement,
            prop: KProperty<*>,
        ): ReadOnlyProperty<XmlElement, String> {
            thisRef.validatorMap[prop.name] = { v ->
                v ?: default
                ?: throw HandlerException("${
                    thisRef::class.simpleName!!.replaceFirstChar { it.lowercase() }
                } node missing '${prop.name}' attribute")
            }
            return ReadOnlyProperty { ref, property ->
                requireNotNull(ref.attributeMap[property.name]) {
                    "${ref.javaClass.simpleName.replaceFirstChar { it.lowercase() }} node is missing '${prop.name}' attribute"
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

