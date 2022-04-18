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
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.NotSpecifyCrudEntityBadDao> {
            assertErrorMessageEquals("Method list requires Dao.crudEntity to be specified")
        }
    }
}