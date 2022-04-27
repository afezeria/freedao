package test.java.integration.core.compile.failure.method.style.named

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {
    @Test
    fun `return type is not Integer and not Long`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.prefix.CountReturnDoubleBadDao> {
            assertErrorMessageEquals("The return type of count method must be Integer or Long")
        }
    }

    @Test
    fun `count method cannot contain sort`() {
        compileFailure<CountMethodContainSortBadDao> {
            assertErrorMessageEquals("count method cannot contain a sort")
        }
    }
}