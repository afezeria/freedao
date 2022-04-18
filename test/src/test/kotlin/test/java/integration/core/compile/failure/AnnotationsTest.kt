package test.java.integration.core.compile.failure

import com.github.afezeria.freedao.Long2IntegerResultHandler
import org.junit.Test
import test.BaseTest
import test.Enum2StringParameterTypeHandler
import test.InvalidParameterTypeHandlerEntity

/**
 *
 * @author afezeria
 */
class AnnotationsTest : BaseTest() {
    @Test
    fun invalidParameterTypeHandler() {
        compileFailure<test.java.integration.core.compile.failure.InvalidParameterTypeHandlerBadDao> {
            assertErrorMessageEquals("The parameter type handler ${Enum2StringParameterTypeHandler::class.qualifiedName} and the type of field ${InvalidParameterTypeHandlerEntity::id.name} do not match")
        }
    }

    @Test
    fun invalidResultTypeHandler() {
        compileFailure<test.java.integration.core.compile.failure.InvalidResultTypeHandlerBadDao> {
            assertErrorMessageEquals("The result type handler ${Long2IntegerResultHandler::class.qualifiedName} and the type of field ${InvalidParameterTypeHandlerEntity::id.name} do not match")
        }
    }
}