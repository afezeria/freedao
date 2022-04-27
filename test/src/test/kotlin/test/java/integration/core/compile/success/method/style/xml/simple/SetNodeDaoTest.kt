package test.java.integration.core.compile.success.method.style.xml.simple

import org.junit.Test
import test.BaseTest
import test.Person
import java.time.LocalDateTime
import kotlin.test.assertFails

/**
 *
 * @author afezeria
 */
class SetNodeDaoTest : BaseTest() {
    @Test
    fun update() {
        initData(
            Person(1, "a", active = true),
        )

        val impl = getJavaDaoInstance<SetNodeDao>()
        assertFails {
            impl.update(1, null, null, null)
        }
        val count1 = impl.update(1, "b", null, null)
        val find1 = env.find("person", "id = 1")[0]
        assert(count1 == 1)
        assert(find1["name"] == "b")

        val count2 = impl.update(1, null, false, null)
        val find2 = env.find("person", "id = 1")[0]
        assert(count2 == 1)
        assert(find2["active"] == false)

        val count3 = impl.update(1, null, null, LocalDateTime.of(2000, 1, 1, 0, 0))
        val find3 = env.find("person", "id = 1")[0]
        assert(count3 == 1)
        assert(find3["when_created"] == LocalDateTime.of(2000, 1, 1, 0, 0))

        val count4 = impl.update(1, "c", true, LocalDateTime.of(2001, 1, 1, 0, 0))
        val find4 = env.find("person", "id = 1")[0]
        assert(count4 == 1)
        assert(find4["name"] == "c")
        assert(find4["active"] == true)
        assert(find4["when_created"] == LocalDateTime.of(2001, 1, 1, 0, 0))
    }
}