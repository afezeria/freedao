package com.github.afezeria.freedao.processor.core.method

import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

/**
 *
 */
class ParameterHelper(val method: MethodModel) {
    val element: ExecutableElement = method.element
    val size: Int = element.parameters.size

    /**
     * 是否能提供params要求的参数
     * @param params List<Pair<String, TypeMirror>>
     * @return Boolean
     */
    fun matchRequirement(params: List<Pair<String, TypeMirror>>): Boolean {

        TODO()
    }
}