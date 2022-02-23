package test.java.success.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.crud.count.ReturnIntegerCountDao
import test.java.success.core.method.style.crud.count.ReturnLongCountDao

/**
 *
 */
class CountTest : BaseTest() {


    @Test
    fun returnInt() {
        initData(Person(1, "a"))

        val impl = getJavaDaoInstance<ReturnIntegerCountDao>()
        val count = impl.count()
        assert(count == 1)
    }

    @Test
    fun returnLong() {
        initData(Person(1, "a"))
        val impl = getJavaDaoInstance<ReturnLongCountDao>()
        val count = impl.count()
        assert(count == 1L)
    }
}