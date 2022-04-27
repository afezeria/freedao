package test.java.integration.core.compile.failure

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
        compileFailure<InvalidParameterTypeHandlerBadDao> {
            assertErrorMessageEquals("The parameter type handler ${Enum2StringParameterTypeHandler::class.qualifiedName} and the type of field ${InvalidParameterTypeHandlerEntity::id.name} do not match")
        }
    }

    @Test
    fun invalidResultTypeHandler() {
        compileFailure<InvalidResultTypeHandlerBadDao> {
            assertErrorMessageEquals("The result type handler ${io.github.afezeria.freedao.Long2IntegerResultHandler::class.qualifiedName} and the type of field ${InvalidParameterTypeHandlerEntity::id.name} do not match")
        }
    }
}