package test.java

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ExtendTest : BaseTest() {
    @Test
    fun all() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<SubDao>()
        val list = impl.all()
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }

    @Test
    fun insert() {
        initData<Person>()
        val impl = getJavaDaoInstance<SubDao>()
        val entity = Person(name = "a")
        val updateCount = impl.insert(entity)
        assert(updateCount == 1)
        assert(entity.id != null)
        env.find("person", "id = ${entity.id}")[0].let {
            assert(it["when_created"] == null)
        }
    }

}