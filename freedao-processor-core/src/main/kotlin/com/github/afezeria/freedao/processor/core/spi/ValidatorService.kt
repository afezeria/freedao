package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.Model

/**
 * provide implementation specific validation rules
 *
 * @author afezeria
 */
interface ValidatorService {
    fun validation(model: Model<*>)
}