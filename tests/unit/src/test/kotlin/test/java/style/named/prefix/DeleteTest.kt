package test.java.style.named.prefix

import org.junit.Test
import test.BaseTest
import test.java.style.named.prefix.DeleteByNameDao

/**
 *
 * @author afezeria
 */
class DeleteTest : BaseTest() {
    @Test
    fun deleteByName() {
        initTable("person", listOf(mapOf("name" to "a"), mapOf("name" to "b")))
        val impl = getJavaDaoInstance<DeleteByNameDao>()
        val updateCount = impl.deleteByName("a")
        assert(updateCount == 1)
        assert(env.find("person").size == 1)
    }
}