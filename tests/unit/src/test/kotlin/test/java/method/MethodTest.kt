package test.java.method

import org.junit.Test
import test.BaseTest
import test.errorMessages

/**
 *
 * @author afezeria
 */
class MethodTest : BaseTest() {
    @Test
    fun invalidMethodDeclareError() {
        compileFailure<InvalidMethodDeclareBadDao> {
            assert(
                errorMessages.any { it == "Invalid method declare" }
            )
        }
    }

    @Test
    fun methodHasTypeParameterError() {
        compileFailure<MethodHasTypeParameterBadDao> {
            assert(
                errorMessages.any { it == "Method cannot have TypeParameter" }
            )
        }
    }

}