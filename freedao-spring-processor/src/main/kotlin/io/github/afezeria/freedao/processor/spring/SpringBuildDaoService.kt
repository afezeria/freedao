package io.github.afezeria.freedao.processor.spring

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import io.github.afezeria.freedao.classic.runtime.AbstractDao
import io.github.afezeria.freedao.classic.runtime.context.DaoContext
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.spi.BuildDaoService
import io.github.afezeria.freedao.processor.core.type
import io.github.afezeria.freedao.processor.core.typeName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.lang.model.element.Modifier

/**
 *
 */
class SpringBuildDaoService(override val order: Int = 20) : BuildDaoService {

    override fun build(daoHandler: DaoHandler, builder: TypeSpec.Builder) {
        builder.addAnnotation(Component::class.java)

        builder.addMethod(
            MethodSpec.methodBuilder(AbstractDao::setContext.name)
                .addAnnotation(Autowired::class.java)
                .addAnnotation(Override::class.java)
                .addParameter(DaoContext::class.type.typeName, "context")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addStatement("super.setContext(context)")
                .build()
        )

    }
}