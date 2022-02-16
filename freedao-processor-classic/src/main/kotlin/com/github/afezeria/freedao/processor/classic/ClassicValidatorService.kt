package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.method.MethodModel
import com.github.afezeria.freedao.processor.core.spi.ValidatorService

/**
 *
 * @author afezeria
 */
class ClassicValidatorService : ValidatorService {
    override fun validation(methodModel: MethodModel) {
        AutoFillStruct.validation(methodModel)
        EnableAutoFill.validation(methodModel)
    }
}