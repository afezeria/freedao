package io.github.afezeria.freedao.processor.core.processor.apt

import com.squareup.javapoet.MethodSpec
import io.github.afezeria.freedao.processor.core.processor.LazyMethod
import io.github.afezeria.freedao.processor.core.processor.LazyParameter
import io.github.afezeria.freedao.processor.core.processor.LazyType
import io.github.afezeria.freedao.processor.core.processor.Modifier
import javax.lang.model.element.ExecutableElement

/**
 *
 * @author afezeria
 */
class AptLazyMethod(val executableElement: ExecutableElement, override val owner: LazyType) :
    AptAnnotated(executableElement), LazyMethod {
    override val delegate: Any
        get() = TODO("Not yet implemented")

    override fun buildMethodSpec(): MethodSpec.Builder {
        TODO("Not yet implemented")
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