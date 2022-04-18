package test.java.integration.core.compile.success.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {
    @Test
    fun countByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.CountByNameDao>()
        val count = impl.countByName("a")
        assert(count == 1)
    }

    @Test
    fun countByNameReturnLong() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.CountByNameReturnLongDao>()
        val count = impl.countByName("a")
        assert(count == 1L)
    }

}