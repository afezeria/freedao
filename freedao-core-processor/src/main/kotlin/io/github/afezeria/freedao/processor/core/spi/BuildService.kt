package io.github.afezeria.freedao.processor.core.spi

import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition

/**
 * provide implementation specific validation rules
 *
 * @author afezeria
 */
interface BuildService {
    fun beforeBuildDao(daoHandler: DaoHandler)
    fun beforeBuildMethod(methodHandler: AbstractMethodDefinition)
}