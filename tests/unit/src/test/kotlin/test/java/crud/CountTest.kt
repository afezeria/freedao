package test.java.crud

import org.junit.Test
import test.BaseTest
import test.java.crud.count.PersonCountDao

/**
 *
 */
class CountTest : BaseTest() {


    @Test
    fun success() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))

        val impl = getJavaDaoInstance<PersonCountDao>()
        val count = impl.count()
        assert(count == 1)
    }

}