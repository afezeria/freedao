package test.java.method.style.crud

import org.junit.Test
import test.BaseTest
import test.errorMessages

/**
 *
 * @author afezeria
 */
class CrudMethodTest : BaseTest() {
    @Test
    fun `error, not specify crud entity`() {
        compileFailure<NotSpecifyCrudEntityBadDao> {
            assert(
                errorMessages.any { it == "Method all requires Dao.crudEntity to be specified" }
            )
        }
    }
}