package test.java.failure.core.method.style.crud

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class CrudMethodTest : BaseTest() {
    @Test
    fun `not specify crud entity`() {
        compileFailure<NotSpecifyCrudEntityBadDao> {
            assertErrorMessageEquals("Method all requires Dao.crudEntity to be specified")
        }
    }
}