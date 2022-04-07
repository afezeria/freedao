package test.java.success.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.named.prefix.CountByNameDao
import test.java.success.core.method.style.named.prefix.CountByNameReturnLongDao

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {
    @Test
    fun countByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<CountByNameDao>()
        val count = impl.countByName("a")
        assert(count == 1)
    }

    @Test
    fun countByNameReturnLong() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<CountByNameReturnLongDao>()
        val count = impl.countByName("a")
        assert(count == 1L)
    }

}