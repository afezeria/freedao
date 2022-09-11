package io.github.afezeria.freedao.classic.processor

import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.classic.runtime.AbstractDao
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.spi.BuildDaoService


/**
 *
 */
class ClassicBuildDaoService(override val order: Int = 10) : BuildDaoService {

    override fun build(daoHandler: DaoHandler, builder: TypeSpec.Builder) {
        builder.superclass(AbstractDao::class.java)
    }

}