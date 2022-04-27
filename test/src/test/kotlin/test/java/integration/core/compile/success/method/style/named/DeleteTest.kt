package test.java.integration.core.compile.success.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.integration.core.compile.success.method.style.named.prefix.DeleteByNameDao
import test.java.integration.core.compile.success.method.style.named.prefix.RemoveByNameDao

/**
 *
 * @author afezeria
 */
class DeleteTest : BaseTest() {
    @Test
    fun removeByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl =
            getJavaDaoInstance<RemoveByNameDao>()
        val updateCount = impl.removeByName("a")
        assert(updateCount == 1)
        assert(env.find("person").size == 1)
    }

    @Test
    fun deleteByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl =
            getJavaDaoInstance<DeleteByNameDao>()
        val updateCount = impl.deleteByName("a")
        assert(updateCount == 1)
        assert(env.find("person").size == 1)
    }
}