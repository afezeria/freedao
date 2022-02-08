package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.annotation.XmlTemplate
import com.github.afezeria.freedao.processor.core.DaoModel
import com.github.afezeria.freedao.processor.core.HandlerException
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class XmlTemplateMethod(
    element: ExecutableElement, daoModel: DaoModel,
) : MethodModel(element, daoModel) {

    init {
        if (element.getAnnotation(XmlTemplate::class.java).value.isBlank()) {
            throw HandlerException("invalid xml template")
        }
    }

    override fun getTemplate(): String {
        return element.getAnnotation(XmlTemplate::class.java).value
    }

    companion object {
        fun match(element: Element): Boolean {
            return element.getAnnotation(XmlTemplate::class.java) != null
        }
    }

}