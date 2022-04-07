package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoHandler
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import javax.lang.model.element.ExecutableElement

/**
 *
 * @author afezeria
 */
interface MethodFactory {
    fun order(): Int
    fun create(
        element: ExecutableElement,
        daoHandler: DaoHandler,
    ): MethodHandler?
}