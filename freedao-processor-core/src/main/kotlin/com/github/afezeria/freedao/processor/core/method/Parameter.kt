package com.github.afezeria.freedao.processor.core.method

import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 *
 */
class Parameter(
    val index: Int,
    val name: String,
    val type: TypeMirror,
    val variableElement: VariableElement,
)