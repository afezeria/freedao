package test.java.integration.core.compile.failure.method

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class MethodTest : BaseTest() {
    @Test
    fun invalidMethodDeclare() {
        compileFailure<InvalidMethodDeclareBadDao> {
            assertErrorMessageEquals("Invalid method declare")
        }
    }

    @Test
    fun invalidParameterName() {
        compileFailure<InvalidParameterNameBadDao> {
            assertErrorMessageEquals("Invalid parameter name:_test")
        }
    }

    @Test
    fun methodHasTypeParameter() {
        compileFailure<MethodHasTypeParameterBadDao> {
            assertErrorMessageEquals("Method cannot have TypeParameter")
        }
    }

}