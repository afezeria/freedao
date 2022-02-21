package test.java.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages
import test.java.method.style.crud.count.ReturnIntegerCountDao
import test.java.method.style.crud.count.ReturnLongCountDao
import test.java.method.style.crud.count.ReturnStringCountBadDao

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

    @Test
    fun returnStringError() {
        compileFailure<ReturnStringCountBadDao> {
            assert(
                errorMessages.contains("The return type of count method must be Integer or Long")
            )
        }
    }
}