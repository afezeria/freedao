package com.github.afezeria.freedao.processor.core.template

import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.MainProcessor
import com.github.afezeria.freedao.processor.core.template.PositionalXMLReader.COLUMN_NUMBER_KEY_NAME
import com.github.afezeria.freedao.processor.core.template.PositionalXMLReader.LINE_NUMBER_KEY_NAME
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
            //忽略idea警告，这里的attributes可能为null
            //不能用hasAttributes()判断，当attributes不为null但大小为0时，hashAttributes()会返回false
            attributes?.apply {
                //检查XmlElement子类中用Attribute声明的代理属性是否为空并赋值
                for ((name, validator) in validatorMap) {
                    attributeMap[name] = validator(getNamedItem(name)?.nodeValue)
                }
                //将xml节点中存在的但子类中未声明的属性添加到attributeMap中
                //暂时没用
                //这是给动态属性预留的，也许某个子类会需要读取用户声明的所有属性
                (0 until length).forEach {
                    item(it)?.takeIf { validatorMap.containsKey(it.nodeName).not() }
                        ?.let { attributeMap[it.nodeName] = it.nodeValue }
                }
            }

            childNodes.takeIf { hasChildNodes() }?.apply {
                (0 until length).forEach { idx ->
                    item(idx).let { child ->
                        createElement(child).let {
                            //当前使用sax解析xml，sax不会区分cdata和text元素
                            // 相邻的cdata和text会被解析成一个text节点，所以这里现在不需要合并text节点的操作
//                            if (it is TextElement && children.isNotEmpty() && children.last() is TextElement) {
//                                //合并多个相邻的text节点
//                                (children.last() as TextElement).append(it)
//                            } else {
//                                it.parent = this@XmlElement
//                                it.init(child)
//                                children += it
//                            }
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

    private fun createElement(node: Node): XmlElement {
        //用DOM解析时会区分comment节点和cdata节点，所以else需要返回null
        //用SAX解析时xml中的cdata元素拿到的是text节点，comment节点会被忽略
//        return when (node.nodeType) {
//            //common text node
//            3.toShort() -> TextElement(false, node.textContent)
//            //cdata node
//            4.toShort() -> TextElement(true, node.textContent)
//            //element node
//            1.toShort() -> elementBuilderMap[node.nodeName.replaceFirstChar { it.uppercaseChar() }]?.call()
//                ?: throw HandlerException("Invalid node:${node.nodeName}")
//            else -> null
//        }
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
            "[line:${xmlNode.getUserData(LINE_NUMBER_KEY_NAME)}, column:${
                xmlNode.getUserData(COLUMN_NUMBER_KEY_NAME)
            }, element:${xmlNode.nodeName}] $msg"
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
        ): ReadOnlyProperty<XmlElement, String> {
            thisRef.validatorMap[prop.name] = { v ->
                v ?: default
                ?: throwWithPosition("missing required attribute:${prop.name}")
            }
            return ReadOnlyProperty { ref, property ->
                requireNotNull(ref.attributeMap[property.name])
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

