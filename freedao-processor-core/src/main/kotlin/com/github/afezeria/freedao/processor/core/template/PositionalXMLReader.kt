package com.github.afezeria.freedao.processor.core.template

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.Attributes
import org.xml.sax.Locator
import org.xml.sax.helpers.DefaultHandler
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

/**
 * copy from [link](https://stackoverflow.com/a/57161301)
 */
object PositionalXMLReader {
    const val LINE_NUMBER_KEY_NAME = "lineNumber"
    const val COLUMN_NUMBER_KEY_NAME = "columnNumber"

    fun readXML(`is`: InputStream?): Document {
        val doc: Document
        val parser: SAXParser
        val factory = SAXParserFactory.newInstance()
        parser = factory.newSAXParser()
        val docBuilderFactory = DocumentBuilderFactory.newInstance()
        val docBuilder = docBuilderFactory.newDocumentBuilder()
        doc = docBuilder.newDocument()
        val handler = LineNumberHandler(doc)
        parser.parse(`is`, handler)
        return doc
    }

    class LineNumberHandler(val doc: Document) : DefaultHandler() {
        private val elementStack: Stack<Element> = Stack<Element>()
        private val textBuffer = StringBuilder()
        lateinit var locator: Locator

        override fun setDocumentLocator(locator: Locator) {
            this.locator = locator
        }

        override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes) {
            addTextIfNeeded()
            val el: Element = doc.createElement(qName)
            for (i in 0 until attributes.length) {
                el.setAttribute(attributes.getQName(i), attributes.getValue(i))
            }
            el.setUserData(LINE_NUMBER_KEY_NAME, locator.lineNumber, null)
            el.setUserData(COLUMN_NUMBER_KEY_NAME, locator.columnNumber, null)
            elementStack.push(el)
        }

        override fun endElement(uri: String?, localName: String?, qName: String?) {
            addTextIfNeeded()
            val closedEl: Element = elementStack.pop()
            if (elementStack.isEmpty()) {
                doc.appendChild(closedEl)
            } else {
                val parentEl: Element = elementStack.peek()
                parentEl.appendChild(closedEl)
            }
        }

        override fun characters(ch: CharArray?, start: Int, length: Int) {
            textBuffer.append(ch, start, length)
        }

        private fun addTextIfNeeded() {
            if (textBuffer.isNotEmpty()) {
                val el: Element = elementStack.peek()
                val textNode: Node = doc.createTextNode(textBuffer.toString())
                el.appendChild(textNode)
                textBuffer.delete(0, textBuffer.length)
            }
        }
    }
}

fun parseXml(text: String): Document {
    return PositionalXMLReader.readXML(ByteArrayInputStream(text.toByteArray()))
}

//fun main() {
//    // read in the xml document
//    // read in the xml document
//    val str = """
//        <select>
//        select * from
//        <where>
//        <![CDATA[
//abc        <select></select>
//        ]]>
//        <if test='name!=null'>
//        and name = #{name}
//        </if>
//            <if test='id > 1'>
//            and id = #{id}
//            </if>
//        </where>
//        </select>
//
//    """.trimIndent()
//    val stream = ByteArrayInputStream(str.toByteArray())
//    val readXML = PositionalXMLReader.readXML(stream)
//    visitDocument(readXML) {
//        println("===========")
//        println("""
//            ${it.nodeName} ${it.getUserData(LINE_NUMBER_KEY_NAME)} ${it.getUserData(COLUMN_NUMBER_KEY_NAME)}
//            ${it.textContent}
//        """.trimIndent())
//    }
//    println()
//}
//
//fun visitDocument(document: Node, fn: (Node) -> Unit) {
//    fn(document)
//    (0 until document.childNodes.length).forEach {
//        visitDocument(document.childNodes.item(it), fn)
//    }
//}