package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.DaoModel
import com.github.afezeria.freedao.processor.core.spi.BuildDaoService
import com.github.afezeria.freedao.processor.core.type
import com.github.afezeria.freedao.runtime.classic.DaoContext
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.lang.model.element.Modifier


/**
 *
 */
class ClassicBuildDaoService(override val order: Int = 10) : BuildDaoService {

    override fun build(daoModel: DaoModel, builder: TypeSpec.Builder) {
        builder.addField(
            FieldSpec.builder(DaoContext::class.java, contextVar)
                .addModifiers(Modifier.PRIVATE)
                .build(),
        )
        builder.fieldSpecs.add(
            0,
            FieldSpec.builder(Logger::class.java, logVar)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL,
                    Modifier.STATIC)
                .initializer("\$T.getLogger(${daoModel.implClassName}.class)", LoggerFactory::class.type).build()
        )
    }

}