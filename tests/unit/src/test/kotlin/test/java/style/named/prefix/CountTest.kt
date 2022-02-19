package test.java.style.named.prefix

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages
import test.java.style.crud.count.ReturnStringCountBadDao

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

    @Test
    fun `error, count return double`(){
        compileFailure<CountReturnDoubleBadDao> {
            assert(
                errorMessages.contains("The return type of count method must be Integer or Long")
            )
        }
    }
}