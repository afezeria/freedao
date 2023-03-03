package io.github.afezeria.freedao.processor.core.spi

import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition
import io.github.afezeria.freedao.processor.core.processor.LazyMethod

/**
 *
 * @author afezeria
 */
interface MethodFactory {
    fun order(): Int
    fun create(
        daoHandler: DaoHandler,
        method: LazyMethod,
    ): AbstractMethodDefinition?
}