package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.processor.core.ObjectModel
import com.github.afezeria.freedao.processor.core.typeName
import com.github.afezeria.freedao.processor.core.typeUtils
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 *
 */
class Parameter(
    val index: Int,
    val name: String,
    val model: ObjectModel,
    val variableElement: VariableElement,
) {

    fun match(name: String, type: TypeMirror): Boolean {
        return this.name == name && typeUtils.isAssignable(this.model.typeMirror, type)
    }

    fun toDeclare(kotlinStyle: Boolean = false) {
        if (kotlinStyle) {
            "$name: ${model.typeMirror.typeName}"
        } else {
            "${model.typeMirror.typeName} $name"
        }
    }

}