package io.github.afezeria.freedao.classic.processor

import io.github.afezeria.freedao.Long2IntegerResultHandler
import io.github.afezeria.freedao.StatementType
import io.github.afezeria.freedao.processor.core.*
import io.github.afezeria.freedao.processor.core.method.MethodHandler
import io.github.afezeria.freedao.processor.core.spi.BuildService
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind

/**
 *
 * @author afezeria
 */
class ClassicBuildService : BuildService {
    override fun beforeBuildDao(daoHandler: DaoHandler) {
    }

    override fun beforeBuildMethod(methodHandler: MethodHandler) {
        val returnType = methodHandler.resultHelper.returnType
        when (methodHandler.statementType) {
            StatementType.SELECT -> {
                if (returnType is PrimitiveType
                ) {
                    if (!returnType.isSameType(typeUtils.getPrimitiveType(TypeKind.INT))
                        && !returnType.isSameType(typeUtils.getPrimitiveType(TypeKind.LONG))
                    ) {
                        throw HandlerException("select method cannot return primitive type other than int and long")
                    }
                    if (returnType.isSameType(typeUtils.getPrimitiveType(TypeKind.INT)) && methodHandler.mappings.isEmpty()) {
                        methodHandler.mappings.add(
                            MappingData(
                                source = "",
                                target = "",
                                typeHandler = Long2IntegerResultHandler::class.type,
                                targetType = Int::class.type,
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