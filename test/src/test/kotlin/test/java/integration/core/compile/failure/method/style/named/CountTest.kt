package test.java.integration.core.compile.failure.method.style.named

import org.junit.Test
import test.BaseTest
import test.java.integration.core.compile.failure.method.style.named.prefix.CountReturnDoubleBadDao

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {
    @Test
    fun `return type is not Integer and not Long`() {
        compileFailure<CountReturnDoubleBadDao> {
            assertErrorMessageEquals("The return type of count method must be Integer/int or Long/long")
        }
    }

    @Test
    fun `count method cannot contain sort`() {
        compileFailure<CountMethodContainSortBadDao> {
            assertErrorMessageEquals("count method cannot contain a sort")
        }
    }
}