package test.java.failure.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.java.failure.core.method.style.named.prefix.CountReturnDoubleBadDao

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {
    @Test
    fun `return type is not Integer and not Long`() {
        compileFailure<CountReturnDoubleBadDao> {
            assertErrorMessageEquals("The return type of count method must be Integer or Long")
        }
    }
}