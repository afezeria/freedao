package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.boxed
import io.github.afezeria.freedao.processor.core.isSameType
import io.github.afezeria.freedao.processor.core.method.MethodHandler
import io.github.afezeria.freedao.processor.core.spi.BuildService
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
                io.github.afezeria.freedao.StatementType.SELECT -> {
                    if (resultHelper.returnType is PrimitiveType) {
                        throw HandlerException("select method cannot return primitive type")
                    }
                }
                io.github.afezeria.freedao.StatementType.INSERT, io.github.afezeria.freedao.StatementType.UPDATE, io.github.afezeria.freedao.StatementType.DELETE -> {
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