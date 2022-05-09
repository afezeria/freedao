package test.java.integration.core.compile.success.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class JavaNodeDaoTest : BaseTest() {
    @Test
    fun query() {
        initData(
            Person(1, "ab", active = true),
            Person(2, "bb", active = true),
        )
        val impl = getJavaDaoInstance<JavaNodeDao>()
        val list1 = impl.query("a")
        assert(list1.size == 1)
        assertContentEquals(list1.map { it.id }, listOf(1))
    }
}