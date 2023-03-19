package io.github.afezeria.freedao.processor.core.processor.apt

import com.squareup.javapoet.MethodSpec
import io.github.afezeria.freedao.processor.core.processor.*
import javax.lang.model.element.ExecutableElement

/**
 *
 * @author afezeria
 */
class AptLazyMethod(executableElement: ExecutableElement, override val owner: LazyType) :
    AptAnnotated(executableElement), LazyMethod {

    override val delegate: ExecutableElement = executableElement

    override fun buildMethodSpec(): MethodSpec.Builder {
        return typeService.createMethodSpecBuilder(this)
    }

    override val typeParameters: List<LazyType>
        get() = TODO("Not yet implemented")
    override val returnType: LazyType
        get() = TODO("Not yet implemented")
    override val qualifiedName: String
        get() = TODO("Not yet implemented")
    override val simpleName: String
        get() = TODO("Not yet implemented")
    override val modifiers: List<Modifier>
        get() = TODO("Not yet implemented")
    override val parameters: List<LazyParameter>
        get() = TODO("Not yet implemented")
}