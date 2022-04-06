package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoHandler
import com.github.afezeria.freedao.processor.core.method.MethodHandler

/**
 * provide implementation specific validation rules
 *
 * @author afezeria
 */
interface BuildService {
    fun beforeBuildDao(daoHandler: DaoHandler)
    fun beforeBuildMethod(methodHandler: MethodHandler)
}