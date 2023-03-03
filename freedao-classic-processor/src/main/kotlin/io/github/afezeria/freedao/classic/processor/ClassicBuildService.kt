package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.Long2IntegerResultHandler
import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.processor.core.DaoHandler
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.MappingData
import io.github.afezeria.freedao.processor.core.method.AbstractMethodDefinition
import io.github.afezeria.freedao.processor.core.processor.*
import io.github.afezeria.freedao.processor.core.spi.BuildService

/**
 *
 * @author afezeria
 */
class ClassicBuildService : BuildService {
    override fun beforeBuildDao(daoHandler: DaoHandler) {
    }

    override fun beforeBuildMethod(methodHandler: AbstractMethodDefinition) {
        val returnType = methodHandler.returnType
        when (methodHandler.statementType) {
            StatementType.SELECT -> {
                if (returnType is PrimitiveType
                ) {
                    if (!returnType.isSameType(typeService.getPrimitiveType(PrimitiveTypeEnum.INT))
                        && !returnType.isSameType(typeService.getPrimitiveType(PrimitiveTypeEnum.LONG))
                    ) {
                        throw HandlerException("select method cannot return primitive type other than int and long")
                    }
                    if (returnType.isSameType(typeService.getPrimitiveType(PrimitiveTypeEnum.INT)) && methodHandler.mappings.isEmpty()) {
                        methodHandler.mappings.add(
                            MappingData(
                                source = "",
                                target = "",
                                typeHandlerLA = Long2IntegerResultHandler::class.typeLA,
                                targetTypeLA = Int::class.typeLA,
                            )
                        )
                    }
                }
            }
            StatementType.INSERT, StatementType.UPDATE, StatementType.DELETE -> {
                if (!returnType.boxed().isSameType(Int::class)
                    && !returnType.boxed().isSameType(Long::class)
                ) {
                    throw HandlerException("The return type of insert/update/delete method must be Integer or Long")
                }
            }
        }
        AutoFillStruct.validation(methodHandler)
        EnableAutoFill.validation(methodHandler)
        ContextParameter.init(methodHandler)

    }
}