package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.ParameterTypeHandler
import com.github.afezeria.freedao.ResultTypeHandler
import com.github.afezeria.freedao.annotation.Column
import javax.lang.model.type.DeclaredType

/**
 * @author afezeria
 */

class ColumnAnn(column: Column?, fieldName: String) {
    val name: String
    val exist: Boolean
    val insert: Boolean
    val update: Boolean
    val parameterTypeHandle: DeclaredType
    val resultTypeHandle: DeclaredType

    init {
        column.let {
            name = it?.name?.takeIf { it.isNotBlank() } ?: fieldName.toSnakeCase()
            exist = it?.exist ?: true
            insert = it?.insert ?: true
            update = it?.update ?: true
            parameterTypeHandle = it?.mirroredType { parameterTypeHandle } ?: ParameterTypeHandler::class.type
            resultTypeHandle = it?.mirroredType { resultTypeHandle } ?: ResultTypeHandler::class.type
        }
    }
}
