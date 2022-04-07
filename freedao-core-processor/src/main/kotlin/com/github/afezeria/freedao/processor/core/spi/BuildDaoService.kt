package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoHandler
import com.squareup.javapoet.TypeSpec

/**
 *
 */
interface BuildDaoService {
    val order: Int
    fun build(daoHandler: DaoHandler, builder: TypeSpec.Builder)
}