package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.annotation.XmlTemplate
import com.github.afezeria.freedao.processor.core.DaoModel
import com.github.afezeria.freedao.processor.core.HandlerException
import javax.lang.model.element.ExecutableElement

class XmlTemplateMethod private constructor(
    element: ExecutableElement, daoModel: DaoModel,
) : MethodModel(element, daoModel) {

    init {
        if (element.getAnnotation(XmlTemplate::class.java).value.isBlank()) {
            throw HandlerException("Xml template cannot be blank")
        }
    }

    override fun getTemplate(): String {
        return element.getAnnotation(XmlTemplate::class.java).value
    }

    companion object {
        operator fun invoke(element: ExecutableElement, daoModel: DaoModel): XmlTemplateMethod? {
            return if (element.getAnnotation(XmlTemplate::class.java) != null) {
                XmlTemplateMethod(element, daoModel)
            } else {
                null
            }
        }

    }

}