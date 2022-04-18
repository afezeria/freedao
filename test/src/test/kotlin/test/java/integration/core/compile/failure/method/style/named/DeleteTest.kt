package test.java.integration.core.compile.failure.method.style.named

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class DeleteTest : BaseTest() {
    @Test
    fun `delete method cannot contain sort`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.DeleteMethodContainSortBadDao> {
            assertErrorMessageEquals("delete method cannot contain a sort")
        }
    }
}