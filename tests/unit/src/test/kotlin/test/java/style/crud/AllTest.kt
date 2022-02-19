package test.java.style.crud

import com.github.afezeria.freedao.processor.core.typeName
import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages
import test.java.style.crud.all.AllDao
import test.java.style.crud.all.ReturnStringAllDao
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class AllTest : BaseTest() {
    @Test
    fun all() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<AllDao>()
        val list = impl.all()
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `invalid return type`(){
        compileFailure<ReturnStringAllDao> {
            assert(
                errorMessages.contains("The return type must be assignable to Collection<${Person::class.qualifiedName}>")
            )
        }
    }
}