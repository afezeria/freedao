package test.java.integration.classic.compile.failure

import org.junit.Test
import test.BaseTest
import test.errorMessages

/**
 *
 * @author afezeria
 */
class ClassicBuildServiceTests : BaseTest() {
    @Test
    fun `query method return value verification`() {
        compileFailure<QueryMethodReturnCharTypeBadDao> {
            assertErrorMessageEquals("select method cannot return primitive type other than int and long")
        }
    }

    @Test
    fun `update method must return Integer or Long`() {
        compileFailure<UpdateMethodNotReturnCharBadDao> {
            assert(errorMessages.size == 3)
            errorMessages.forEach {
                assert(it == "The return type of insert/update/delete method must be Integer or Long")
            }
        }
    }
}