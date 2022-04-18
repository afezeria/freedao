package test.java.integration.core.compile.success.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.integration.core.compile.success.method.style.crud.count.ReturnIntegerCountDao
import test.java.integration.core.compile.success.method.style.crud.count.ReturnLongCountDao

/**
 *
 */
class CountTest : BaseTest() {


    @Test
    fun returnInt() {
        initData(Person(1, "a"))

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.count.ReturnIntegerCountDao>()
        val count = impl.count(null)
        assert(count == 1)

        val count2 = impl.count(Person())
        assert(count2 == 1)
    }

    @Test
    fun returnLong() {
        initData(Person(1, "a"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.count.ReturnLongCountDao>()
        val count = impl.count(null)
        assert(count == 1L)
    }

    @Test
    fun countByName() {
        initData(Person(1, "a"), Person(2, "a"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.count.ReturnIntegerCountDao>()
        val count = impl.count(Person(name = "a"))
        assert(count == 2)
    }
}