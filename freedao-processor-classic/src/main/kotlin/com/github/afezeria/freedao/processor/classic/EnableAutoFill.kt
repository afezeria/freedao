package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.github.afezeria.freedao.runtime.classic.DisableAutoFill

object EnableAutoFill {
    private fun isInsertMethod(method: MethodHandler): Boolean {
        return method.statementType == StatementType.INSERT || method.statementType == StatementType.UPDATE
//        return when (method) {
//            is Insert -> true
//            is XmlTemplateMethod -> {
//                parseXml(method.element.getAnnotation(XmlTemplate::class.java)!!.value).firstChild.nodeName == "insert"
//            }
//            else -> false
//        }
    }

    fun validation(methodHandler: MethodHandler) {
        if (methodHandler.element.getAnnotation(DisableAutoFill::class.java) != null && !isInsertMethod(methodHandler)) {
            throw HandlerException("EnableAutoFill can only be used on insert method")
        }
    }

    operator fun invoke(method: MethodHandler): Boolean {
        return if (isInsertMethod(method)) {
            method.element.getAnnotation(DisableAutoFill::class.java) == null
        } else {
            false
        }
    }
}