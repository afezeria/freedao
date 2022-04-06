package com.github.afezeria.freedao.processor.classic

import com.github.afezeria.freedao.StatementType
import com.github.afezeria.freedao.processor.core.DaoHandler
import com.github.afezeria.freedao.processor.core.HandlerException
import com.github.afezeria.freedao.processor.core.boxed
import com.github.afezeria.freedao.processor.core.isSameType
import com.github.afezeria.freedao.processor.core.method.MethodHandler
import com.github.afezeria.freedao.processor.core.spi.BuildService
import javax.lang.model.type.PrimitiveType

/**
 *
 * @author afezeria
 */
class ClassicBuildService : BuildService {
    override fun beforeBuildDao(daoHandler: DaoHandler) {
    }

    override fun beforeBuildMethod(methodHandler: MethodHandler) {
        methodHandler.apply {
            when (statementType) {
                StatementType.SELECT -> {
                    if (resultHelper.returnType is PrimitiveType) {
                        throw HandlerException("select method cannot return primitive type")
                    }
                }
                StatementType.INSERT, StatementType.UPDATE, StatementType.DELETE -> {
                    if (!resultHelper.returnType.boxed().isSameType(Int::class)
                        && !resultHelper.returnType.boxed().isSameType(Long::class)
                    ) {
                        throw HandlerException("The return type of insert/update/delete method must be Integer or Long")
                    }
                }
            }
        }
        AutoFillStruct.validation(methodHandler)
        EnableAutoFill.validation(methodHandler)
        ContextParameter.init(methodHandler)

    }
}