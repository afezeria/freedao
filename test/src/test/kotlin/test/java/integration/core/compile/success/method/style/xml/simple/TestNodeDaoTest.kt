package test.java.integration.core.compile.success.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class TestNodeDaoTest : BaseTest() {
    @Test
    fun query() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.xml.simple.TestNodeDao>()
        val list = impl.query("person", "a")
        assert(list.size == 1)
        assertContentEquals(list.map { it["id"] }, listOf(1L))
    }
}