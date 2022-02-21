package com.github.afezeria.freedao.processor.core.method

import com.github.afezeria.freedao.processor.core.ObjectModel
import javax.lang.model.element.VariableElement

/**
 *
 */
class Parameter(
    val index: Int,
    val name: String,
    //todo 所有涉及到这个参数的地方都只是取了ObjectModel的typeMirror属性，这个字段实际应该是TypeMirror类型
    val model: ObjectModel,
    val variableElement: VariableElement,
)