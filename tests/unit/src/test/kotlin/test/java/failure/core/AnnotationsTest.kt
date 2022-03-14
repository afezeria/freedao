package test.java.failure.core

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
        compileFailure<InvalidParameterTypeHandlerBadDao> {
            assertErrorMessageEquals("The parameter type handler ${Enum2StringParameterTypeHandler::class.qualifiedName} and the type of field ${InvalidParameterTypeHandlerEntity::id.name} do not match")
        }
    }

    @Test
    fun invalidResultTypeHandler() {
        compileFailure<InvalidResultTypeHandlerBadDao> {
            assertErrorMessageEquals("The result type handler ${Long2IntegerResultHandler::class.qualifiedName} and the type of field ${InvalidParameterTypeHandlerEntity::id.name} do not match")
        }
    }
}