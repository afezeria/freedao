package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.annotation.XmlTemplate
import com.github.afezeria.freedao.processor.core.DaoHandler
import com.github.afezeria.freedao.processor.core.HandlerException
import javax.lang.model.element.ExecutableElement

class XmlTemplateMethod private constructor(
    element: ExecutableElement, daoHandler: DaoHandler,
) : MethodHandler(element, daoHandler) {

    init {
        if (element.getAnnotation(XmlTemplate::class.java).value.isBlank()) {
            throw HandlerException("Xml template cannot be blank")
        }
    }

    override fun getTemplate(): String {
        return element.getAnnotation(XmlTemplate::class.java).value
    }

    companion object {
        operator fun invoke(element: ExecutableElement, daoHandler: DaoHandler): XmlTemplateMethod? {
            return if (element.getAnnotation(XmlTemplate::class.java) != null) {
                XmlTemplateMethod(element, daoHandler)
            } else {
                null
            }
        }

    }

}