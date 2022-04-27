package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.classic.runtime.DisableAutoFill
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.method.MethodHandler

object EnableAutoFill {
    private fun isUpdateMethod(method: MethodHandler): Boolean {
        return method.statementType == io.github.afezeria.freedao.StatementType.INSERT || method.statementType == io.github.afezeria.freedao.StatementType.UPDATE
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