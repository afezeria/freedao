package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.processor.core.HandlerException
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 *
 */
open class Parameter(
    val index: Int,
    val name: String,
    val type: TypeMirror,
)

class RealParameter(
    index: Int,
    name: String,
    type: TypeMirror,
    val variableElement: VariableElement,
) : Parameter(index, name, type) {
    init {
        if (!regex.matches(name)) {
            throw HandlerException("Invalid parameter name:$name")
        }
    }

    companion object {
        val regex = "[a-zA-Z0-9]+".toRegex()
    }
}