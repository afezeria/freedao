package test.java.named.cond

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class ConditionTest : BaseTest() {
    @Test
    fun `is`() {
        initData(Person(2, "b"), Person(1, "a"))
        val impl = getJavaDaoInstance<IsDao>()
        val list = impl.queryById(1)
        assert(list.size == 1)
        assert(list[0].name == "a")
    }

    @Test
    fun between() {
        initData(Person(2, "b"), Person(5, "a"))
        val impl = getJavaDaoInstance<BetweenDao>()
        val list = impl.queryByIdBetween(1, 3)
        assert(list.size == 1)
        assert(list[0].id == 2L)
    }
}