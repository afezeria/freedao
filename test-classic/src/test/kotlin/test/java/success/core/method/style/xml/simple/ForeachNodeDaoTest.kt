package test.java.success.core.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ForeachNodeDaoTest : BaseTest() {
    @Test
    fun query() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
            Person(3, "b"),
        )
        val impl = getJavaDaoInstance<ForeachNodeDao>()
        val list = impl.queryIdIn(listOf(1, 3))
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 3))
    }
}