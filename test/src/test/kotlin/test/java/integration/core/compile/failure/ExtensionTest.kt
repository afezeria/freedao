package test.java.integration.core.compile.failure

import org.junit.Test
import test.BaseTest
import test.Enum2StringParameterTypeHandler
import test.errorMessages

/**
 *
 * @author afezeria
 */
class ExtensionTest : BaseTest() {
    @Test
    fun `error class name`() {
        compileFailure<test.java.integration.core.compile.failure.template.extensions.InvalidTypeHandlerBadDao> {
            assert(errorMessages.size == 5)
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler1::class.qualifiedName}, missing method:public static Object handleParameter"))
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler2::class.qualifiedName}, missing method:public static Object handleParameter"))
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler3::class.qualifiedName}, missing method:public static Object handleParameter"))
            assert(errorMessages.contains("Invalid ParameterTypeHandler:${test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler4::class.qualifiedName}, missing method:public static Object handleParameter"))
            assert(errorMessages.contains("${Enum2StringParameterTypeHandler::class.qualifiedName} does not match ${String::class.java.name} type"))

        }
    }
}