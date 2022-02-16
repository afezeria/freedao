package com.github.afezeria.freedao.processor.core.spi

import com.github.afezeria.freedao.processor.core.DaoModel
import com.github.afezeria.freedao.processor.core.method.MethodModel

/**
 * provide implementation specific validation rules
 *
 * @author afezeria
 */
interface ValidatorService {
    fun validation(daoModel: DaoModel) {}
    fun validation(methodModel: MethodModel) {}
}