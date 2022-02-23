package test.java.failure.core.method

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
    fun methodHasTypeParameter() {
        compileFailure<MethodHasTypeParameterBadDao> {
            assertErrorMessageEquals("Method cannot have TypeParameter")
        }
    }

}