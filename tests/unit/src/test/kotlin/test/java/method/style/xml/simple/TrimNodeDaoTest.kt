package test.java.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class TrimNodeDaoTest : BaseTest() {
    @Test
    fun query() {
        initData(
            Person(1, "a", active = true),
            Person(2, "b", active = true),
        )
        val impl = getJavaDaoInstance<TrimNodeDao>()
        val list1 = impl.query(1, 2)
        assert(list1.size == 2)
        assertContentEquals(list1.map { it.id }, listOf(1, 2))
    }
}