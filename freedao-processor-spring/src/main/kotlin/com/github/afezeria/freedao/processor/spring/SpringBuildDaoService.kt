package com.github.afezeria.freedao.processor.spring

import com.github.afezeria.freedao.processor.classic.contextVar
import com.github.afezeria.freedao.processor.core.DaoHandler
import com.github.afezeria.freedao.processor.core.spi.BuildDaoService
import com.squareup.javapoet.TypeSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 */
class SpringBuildDaoService(override val order: Int = 20) : BuildDaoService {

    override fun build(daoHandler: DaoHandler, builder: TypeSpec.Builder) {
        builder.addAnnotation(Component::class.java)
        builder.fieldSpecs.apply {
            val index = indexOfFirst { it.name == contextVar }
            val field = get(index)
                .toBuilder()
                .addAnnotation(Autowired::class.java)
                .build()
            set(index, field)
        }
    }
}