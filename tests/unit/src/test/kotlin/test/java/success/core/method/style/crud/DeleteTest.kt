package test.java.success.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.crud.delete.PersonDeleteDao

/**
 *
 */
class DeleteTest : BaseTest() {
    @Test
    fun success() {
        initData(Person(1, "a"))

        val impl = getJavaDaoInstance<PersonDeleteDao>()
        val updateCount = impl.delete(1)
        assert(updateCount == 1)
    }
}