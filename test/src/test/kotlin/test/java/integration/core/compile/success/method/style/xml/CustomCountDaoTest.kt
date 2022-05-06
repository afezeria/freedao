package test.java.integration.core.compile.success.method.style.xml

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.integration.core.compile.success.CustomCountDao

/**
 *
 * @author afezeria
 */
class CustomCountDaoTest : BaseTest() {
    @Test
    fun customCount() {
        initData(
            Person(1, "a"),
            Person(2, "b"),
        )
        val impl = getJavaDaoInstance<CustomCountDao>()
        val cot = impl.count()
        assert(cot == 2)
    }
}