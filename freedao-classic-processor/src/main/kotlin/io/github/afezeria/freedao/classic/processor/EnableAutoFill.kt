package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.classic.runtime.DisableAutoFill
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition

object EnableAutoFill {
    private fun isUpdateMethod(method: AbstractMethodDefinition): Boolean {
        return method.statementType == StatementType.INSERT || method.statementType == StatementType.UPDATE
    }

    fun validation(methodHandler: AbstractMethodDefinition) {
        if (methodHandler.method.getAnnotation(DisableAutoFill::class) != null && !isUpdateMethod(methodHandler)) {
            throw HandlerException("${DisableAutoFill::class.qualifiedName} can only be used on insert/update method")
        }
    }

    operator fun invoke(method: AbstractMethodDefinition): Boolean {
        return if (isUpdateMethod(method)) {
            method.method.getAnnotation(DisableAutoFill::class) == null
        } else {
            false
        }
    }
}