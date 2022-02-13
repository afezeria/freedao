package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoModel
import com.github.afezeria.freedao.processor.core.method.MethodModel
import javax.lang.model.element.ExecutableElement

/**
 *
 * @author afezeria
 */
interface MethodFactory {
    fun order(): Int
    fun create(
        element: ExecutableElement,
        daoModel: DaoModel,
    ): MethodModel?
}