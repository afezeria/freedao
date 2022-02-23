package test.java.success.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.named.prefix.DeleteByNameDao

/**
 *
 * @author afezeria
 */
class DeleteTest : BaseTest() {
    @Test
    fun deleteByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<DeleteByNameDao>()
        val updateCount = impl.deleteByName("a")
        assert(updateCount == 1)
        assert(env.find("person").size == 1)
    }
}