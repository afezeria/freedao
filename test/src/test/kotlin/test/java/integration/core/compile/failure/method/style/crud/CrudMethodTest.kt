package test.java.integration.core.compile.failure.method.style.crud

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
            assertErrorMessageEquals("Method list requires Dao.crudEntity to be specified")
        }
    }
}