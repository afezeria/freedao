package test.java.success.core.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ChooseNodeDaoTest : BaseTest() {
    @Test
    fun queryByNameIfNameNotNull() {
        initData(
            Person(1, "a", active = true),
            Person(2, "b", active = true),
            Person(3, "b", active = false),
        )
        val impl = getJavaDaoInstance<ChooseNodeDao>()
        val list1 = impl.query(3, null)
        assert(list1.size == 1)
        assertContentEquals(list1.map { it.id }, listOf(3))

        val list2 = impl.query(null, "b")
        assert(list2.size == 2)
        assertContentEquals(list2.map { it.id }, listOf(2, 3))

        val list3 = impl.query(1, "b")
        assert(list3.size == 1)
        assertContentEquals(list3.map { it.id }, listOf(1))

        val list4 = impl.query(null, null)
        assert(list4.size == 2)
        assertContentEquals(list4.map { it.id }, listOf(1, 2))
    }
}