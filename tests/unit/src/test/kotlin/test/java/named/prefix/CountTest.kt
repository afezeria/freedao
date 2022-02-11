package test.java.named.prefix

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class CountTest : BaseTest() {
    @Test
    fun countByName() {
        initTable("person", listOf(mapOf("name" to "a"), mapOf("name" to "b")))
        val impl = getJavaDaoInstance<CountByNameDao>()
        val count = impl.countByName("a")
        assert(count == 1)
    }
}