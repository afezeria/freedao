package io.github.afezeria.freedao.processor.core.processor.apt

import io.github.afezeria.freedao.processor.core.processor.LazyType
import io.github.afezeria.freedao.processor.core.processor.LazyVariable
import io.github.afezeria.freedao.processor.core.processor.Modifier
import javax.lang.model.element.VariableElement

/**
 *
 * @author afezeria
 */
class AptLazyVariable(override val element: VariableElement, override val owner: LazyType) : AptAnnotated(element),
    LazyVariable {
    override val simpleName: String
        get() = TODO("Not yet implemented")
    override val type: LazyType
        get() = TODO("Not yet implemented")
    override val modifier: List<Modifier>
        get() = TODO("Not yet implemented")
    override val delegate: Any
        get() = TODO("Not yet implemented")
}