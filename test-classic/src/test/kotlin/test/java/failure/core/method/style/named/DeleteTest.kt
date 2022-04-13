package test.java.failure.core.method.style.named

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class DeleteTest : BaseTest() {
    @Test
    fun `delete method cannot contain sort`() {
        compileFailure<DeleteMethodContainSortBadDao> {
            assertErrorMessageEquals("delete method cannot contain a sort")
        }
    }
}