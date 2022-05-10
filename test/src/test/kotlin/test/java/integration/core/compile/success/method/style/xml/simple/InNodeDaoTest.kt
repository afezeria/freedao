package test.java.integration.core.compile.success.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class InNodeDaoTest : BaseTest() {
    @Test
    fun query() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
            Person(3, "b"),
        )
        val impl =
            getJavaDaoInstance<InNodeDao>()
        val list = impl.queryIdIn(listOf(1, 3))
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 3))
    }

    @Test
    fun itemAttr() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
            Person(3, "b"),
        )
        val impl =
            getJavaDaoInstance<InNodeWithItemAttrDao>()
        val list = impl.queryInEntity(listOf(Person(1), Person(3)))
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 3))
    }

}