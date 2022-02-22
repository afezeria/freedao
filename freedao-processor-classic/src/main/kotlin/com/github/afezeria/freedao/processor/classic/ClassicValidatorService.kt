package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.github.afezeria.freedao.processor.core.spi.ValidatorService

/**
 *
 * @author afezeria
 */
class ClassicValidatorService : ValidatorService {
    override fun validation(methodHandler: MethodHandler) {
        AutoFillStruct.validation(methodHandler)
        EnableAutoFill.validation(methodHandler)
    }
}