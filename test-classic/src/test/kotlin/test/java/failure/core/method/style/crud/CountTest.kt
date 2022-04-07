package test.java.failure.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.java.failure.core.method.style.crud.count.ReturnStringCountBadDao

/**
 *
 */
class CountTest : BaseTest() {
    @Test
    fun returnString() {
        compileFailure<ReturnStringCountBadDao> {
            assertErrorMessageEquals("The return type of count method must be Integer or Long")
        }
    }
}