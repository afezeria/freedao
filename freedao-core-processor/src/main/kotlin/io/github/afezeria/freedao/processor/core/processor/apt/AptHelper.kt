package io.github.afezeria.freedao.processor.core.processor.apt

import javax.lang.model.element.Modifier

/**
 *
 * @author afezeria
 */
object AptHelper {
    fun modifierConvert(modifier: Modifier): io.github.afezeria.freedao.processor.core.processor.Modifier {
        return io.github.afezeria.freedao.processor.core.processor.Modifier.valueOf(modifier.name)
    }
}