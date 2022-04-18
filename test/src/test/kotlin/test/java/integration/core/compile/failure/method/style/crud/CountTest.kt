package test.java.integration.core.compile.failure.method.style.crud

import org.junit.Test
import test.BaseTest

/**
 *
 */
class CountTest : BaseTest() {
    @Test
    fun returnString() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.count.ReturnStringCountBadDao> {
            assertErrorMessageEquals("The return type of count method must be Integer or Long")
        }
    }
}