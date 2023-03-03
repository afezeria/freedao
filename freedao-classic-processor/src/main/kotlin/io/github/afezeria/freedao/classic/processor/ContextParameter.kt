package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition
import io.github.afezeria.freedao.processor.core.processor.VirtualLazyParameterImpl
import io.github.afezeria.freedao.processor.core.processor.typeLA

/**
 *
 * @author afezeria
 */
object ContextParameter {
    fun init(methodHandler: AbstractMethodDefinition) {
        methodHandler.parameters.apply {
            add(
                VirtualLazyParameterImpl(
                    simpleName = "_context",
                    type = Map::class.typeLA(String::class.typeLA, Any::class.typeLA),
                    index = 0
                )
            )
        }
    }
}