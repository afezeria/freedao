package test.java.failure.core

import org.junit.Test
import test.BaseTest
import test.Enum2StringParameterTypeHandler
import test.errorMessages
import test.java.failure.core.template.extensions.*

/**
 *
 * @author afezeria
 */
class ExtensionTest : BaseTest() {
    @Test
    fun `error class name`() {
        compileFailure<InvalidTypeHandlerBadDao> {
            assert(errorMessages.size == 5)
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${InvalidParameterTypeHandler1::class.qualifiedName}, missing method:handle"))
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${InvalidParameterTypeHandler2::class.qualifiedName}, missing method:handle"))
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${InvalidParameterTypeHandler3::class.qualifiedName}, missing method:handle"))
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${InvalidParameterTypeHandler4::class.qualifiedName}, missing method:handle"))
            assert(errorMessages.contains("${Enum2StringParameterTypeHandler::class.qualifiedName} does not match ${String::class.java.name} type"))

        }
    }
}