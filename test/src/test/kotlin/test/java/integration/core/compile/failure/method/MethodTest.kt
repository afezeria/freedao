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
        compileFailure<test.java.integration.core.compile.failure.method.InvalidMethodDeclareBadDao> {
            assertErrorMessageEquals("Invalid method declare")
        }
    }

    @Test
    fun invalidParameterName() {
        compileFailure<test.java.integration.core.compile.failure.method.InvalidParameterNameBadDao> {
            assertErrorMessageEquals("Invalid parameter name:_test")
        }
    }

    @Test
    fun methodHasTypeParameter() {
        compileFailure<test.java.integration.core.compile.failure.method.MethodHasTypeParameterBadDao> {
            assertErrorMessageEquals("Method cannot have TypeParameter")
        }
    }

}