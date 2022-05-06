package test.java.integration.core.compile.failure.method.style.crud

import org.junit.Test
import test.BaseTest
import test.java.integration.core.compile.failure.method.style.crud.count.ReturnStringCountBadDao

/**
 *
 */
class CountTest : BaseTest() {
    @Test
    fun returnString() {
        compileFailure<ReturnStringCountBadDao> {
            assertErrorMessageEquals("The return type of count method must be Integer/int or Long/long")
        }
    }
}