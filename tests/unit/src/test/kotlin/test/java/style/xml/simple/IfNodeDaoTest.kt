package test.java.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class IfNodeDaoTest : BaseTest() {
    @Test
    fun queryByNameIfNameNotNull() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
        )
        val impl = getJavaDaoInstance<IfNodeDao>()
        var list = impl.queryByNameIfNameNotNull("a")
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))

        list = impl.queryByNameIfNameNotNull(null)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }
}