package test.java.integration.core.compile.success.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Clazz
import test.Person
import test.java.integration.core.compile.success.method.style.crud.delete.DeleteWithCompositePrimaryKeyDao
import test.java.integration.core.compile.success.method.style.crud.delete.PersonDeleteDao
import kotlin.test.assertFails

/**
 *
 */
class DeleteTest : BaseTest() {
    @Test
    fun failureWhenAllFieldAreNull() {
        initData(Person(1, "a"))

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.delete.PersonDeleteDao>()
        assertFails {
            impl.delete(Person())
        }
    }

    @Test
    fun deleteByMultipleField() {
        initData(Clazz(1, 1, "a"), Clazz(1, 2, "a"))

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.delete.DeleteWithCompositePrimaryKeyDao>()
        val delete = impl.delete(Clazz(1, 1))
        assert(delete == 1)
    }

    @Test
    fun deleteById() {
        initData(Clazz(1, 1, "a"), Clazz(1, 2, "a"))

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.delete.DeleteWithCompositePrimaryKeyDao>()
        val delete = impl.delete(Clazz(teacherId = 2))
        assert(delete == 1)
    }
}