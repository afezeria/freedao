package test.java.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages
import test.java.style.crud.delete.EntityWithoutPrimaryKeyDeleteBadDao
import test.java.style.crud.delete.ParametersNotMatchDeleteBadDao
import test.java.style.crud.delete.PersonDeleteDao

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

    @Test
    fun entityWithoutPrimaryKeyError() {
        compileFailure<EntityWithoutPrimaryKeyDeleteBadDao> {
            assert(
                errorMessages.contains("The delete method requires that the Dao.crudEntity has primary key")
            )
        }
    }

    @Test
    fun `parameter types does not match primary key`() {
        compileFailure<ParametersNotMatchDeleteBadDao> {
            assert(
                errorMessages.contains("Missing parameter of type java.lang.Long")
            )
        }
    }
}