package io.github.afezeria.freedao.classic.processor

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.classic.runtime.context.DaoContext
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.spi.BuildDaoService
import io.github.afezeria.freedao.processor.core.type
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.lang.model.element.Modifier


/**
 *
 */
class ClassicBuildDaoService(override val order: Int = 10) : BuildDaoService {

    override fun build(daoHandler: DaoHandler, builder: TypeSpec.Builder) {
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
                .initializer("\$T.getLogger(${daoHandler.implClassName}.class)", LoggerFactory::class.type).build()
        )
    }

}