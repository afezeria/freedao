package io.github.afezeria.freedao.processor.core.method

import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import javax.lang.model.element.ExecutableElement

class XmlTemplateMethod private constructor(
    element: ExecutableElement, daoHandler: DaoHandler,
) : MethodHandler(element, daoHandler) {

    init {
        if (element.getAnnotation(io.github.afezeria.freedao.annotation.XmlTemplate::class.java).value.isBlank()) {
            throw HandlerException("Xml template cannot be blank")
        }
    }

    override fun getTemplate(): String {
        return element.getAnnotation(io.github.afezeria.freedao.annotation.XmlTemplate::class.java).value
    }

    companion object {
        operator fun invoke(element: ExecutableElement, daoHandler: DaoHandler): XmlTemplateMethod? {
            return if (element.getAnnotation(io.github.afezeria.freedao.annotation.XmlTemplate::class.java) != null) {
                XmlTemplateMethod(element, daoHandler)
            } else {
                null
            }
        }

    }

}