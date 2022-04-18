package test.java.integration.core.compile.success

import org.junit.Test
import test.BaseTest
import test.Person
import test.PersonType

/**
 *
 * @author afezeria
 */
class AnnotationTest : BaseTest() {
    @Test
    fun queryWithDefaultEnumTypeHandler() {
        initData<Person>()
        initTable(
            "person",
            listOf(
                mapOf("id" to 1, "type" to PersonType.TEACHER.name),
                mapOf("id" to 2, "type" to PersonType.STUDENT.name),
            )
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.QueryWithDefaultEnumTypeHandlerDao>()
        val list = impl.selectBySearchType(PersonType.TEACHER)
        assert(list.size == 1)
        assert(list[0].type == PersonType.TEACHER)
    }
}