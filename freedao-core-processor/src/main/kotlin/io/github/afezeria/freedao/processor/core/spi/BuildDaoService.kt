package io.github.afezeria.freedao.processor.core.spi

import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.processor.core.DaoHandler

/**
 *
 */
interface BuildDaoService {
    val order: Int
    fun build(daoHandler: DaoHandler, builder: TypeSpec.Builder)
}