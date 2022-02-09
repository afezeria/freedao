package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.Model
import com.github.afezeria.freedao.processor.core.method.MethodModel
import com.github.afezeria.freedao.processor.core.spi.ValidatorService

/**
 *
 * @author afezeria
 */
class ClassicValidatorService : ValidatorService {
    override fun validation(model: Model<*>){
        when (model) {
            is MethodModel -> {
                AutoFillStruct.validation(model)
                EnableAutoFill.validation(model)
            }
            else -> {}
        }
    }

}