package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.processor.core.method.MethodHandler
import io.github.afezeria.freedao.processor.core.method.Parameter
import io.github.afezeria.freedao.processor.core.type

/**
 *
 * @author afezeria
 */
object ContextParameter {
    fun init(methodHandler: MethodHandler) {
        methodHandler.parameters.apply {
            add(
                Parameter(
                    index = size,
                    name = "_context",
                    type = Map::class.type(String::class.type, Any::class.type)
                )
            )
        }
    }
}