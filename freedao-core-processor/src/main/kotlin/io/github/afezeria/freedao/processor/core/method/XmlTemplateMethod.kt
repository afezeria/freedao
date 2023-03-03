package io.github.afezeria.freedao.processor.core.method

import io.github.afezeria.freedao.annotation.XmlTemplate
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.processor.LazyMethod

class XmlTemplateMethod private constructor(
    daoHandler: DaoHandler, method: LazyMethod,
) : AbstractMethodDefinition(daoHandler, method) {


    init {
        if (method.getAnnotation(XmlTemplate::class)!!.value.isBlank()) {
            throw HandlerException("Xml template cannot be blank")
        }
    }

    override fun getTemplate(): String {
        return method.getAnnotation(XmlTemplate::class)!!.value
    }

    companion object {
        operator fun invoke(daoHandler: DaoHandler, method: LazyMethod): XmlTemplateMethod? {
            return if (method.getAnnotation(XmlTemplate::class) != null) {
                XmlTemplateMethod(daoHandler, method)
            } else {
                null
            }
        }

    }

}