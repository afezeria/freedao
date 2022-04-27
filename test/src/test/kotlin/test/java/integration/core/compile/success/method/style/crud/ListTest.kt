package test.java.integration.core.compile.success.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.integration.core.compile.success.method.style.crud.list.ListDao
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ListTest : BaseTest() {
    @Test
    fun list() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<ListDao>()
        val list = impl.list(null)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))

        val list2 = impl.list(Person())
        assert(list2.size == 2)
        assertContentEquals(list2.map { it.id }, listOf(1, 2))
    }

    @Test
    fun listByNonNullField() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<ListDao>()

        val list2 = impl.list(Person(id = 1))
        assert(list2.size == 1)
        assertContentEquals(list2.map { it.id }, listOf(1))
    }

}