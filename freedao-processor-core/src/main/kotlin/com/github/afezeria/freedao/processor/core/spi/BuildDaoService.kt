package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoModel
import com.squareup.javapoet.TypeSpec

/**
 *
 */
interface BuildDaoService {
    val order: Int
    fun build(daoModel: DaoModel, builder: TypeSpec.Builder)
}