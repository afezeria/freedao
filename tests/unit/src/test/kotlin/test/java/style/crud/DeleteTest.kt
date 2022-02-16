package test.java.style.crud

import org.junit.Test
import test.BaseTest
import test.java.style.crud.delete.PersonDeleteDao

/**
 *
 */
class DeleteTest : BaseTest() {


    @Test
    fun success() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))

        val impl = getJavaDaoInstance<PersonDeleteDao>()
        val updateCount = impl.delete(1)
        assert(updateCount == 1)
    }

}