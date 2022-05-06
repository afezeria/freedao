package test.java.integration.core.compile.success.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.integration.core.compile.success.method.style.named.prefix.CountByNameDao
import test.java.integration.core.compile.success.method.style.named.prefix.CountByNameReturnLongDao
import test.java.integration.core.compile.success.method.style.named.prefix.CountByNameReturnPrimitiveIntDao
import test.java.integration.core.compile.success.method.style.named.prefix.CountByNameReturnPrimitiveLongDao

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {

    @Test
    fun countByNameReturnPrimitiveInt() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl =
            getJavaDaoInstance<CountByNameReturnPrimitiveIntDao>()
        val count = impl.countByName("a")
        assert(count == 1)
    }

    @Test
    fun countByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl =
            getJavaDaoInstance<CountByNameDao>()
        val count = impl.countByName("a")
        assert(count == 1)
    }

    @Test
    fun countByNameReturnLong() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl =
            getJavaDaoInstance<CountByNameReturnLongDao>()
        val count = impl.countByName("a")
        assert(count == 1L)
    }

    @Test
    fun countByNameReturnPrimitiveLong() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl =
            getJavaDaoInstance<CountByNameReturnPrimitiveLongDao>()
        val count = impl.countByName("a")
        assert(count == 1L)
    }
}