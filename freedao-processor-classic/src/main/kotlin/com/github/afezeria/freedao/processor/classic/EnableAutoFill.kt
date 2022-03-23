package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.github.afezeria.freedao.runtime.classic.DisableAutoFill

object EnableAutoFill {
    private fun isUpdateMethod(method: MethodHandler): Boolean {
        return method.statementType == StatementType.INSERT || method.statementType == StatementType.UPDATE
    }

    fun validation(methodHandler: MethodHandler) {
        if (methodHandler.element.getAnnotation(DisableAutoFill::class.java) != null && !isUpdateMethod(methodHandler)) {
            throw HandlerException("${DisableAutoFill::class.qualifiedName} can only be used on insert/update method")
        }
    }

    operator fun invoke(method: MethodHandler): Boolean {
        return if (isUpdateMethod(method)) {
            method.element.getAnnotation(DisableAutoFill::class.java) == null
        } else {
            false
        }
    }
}