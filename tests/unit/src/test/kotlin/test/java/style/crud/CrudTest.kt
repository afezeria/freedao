package test.java.style.crud

import org.junit.Test
import test.BaseTest
import test.errorMessages

/**
 *
 * @author afezeria
 */
class CrudTest : BaseTest() {
    @Test
    fun notSpecifyCrudEntityError() {
        compileFailure<NotSpecifyCrudEntityBadDao> {
            assert(
                errorMessages.any { it == "Method all requires Dao.crudEntity to be specified" }
            )
        }
    }
}