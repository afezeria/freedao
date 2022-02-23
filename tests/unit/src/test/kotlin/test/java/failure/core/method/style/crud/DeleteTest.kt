package test.java.failure.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.java.failure.core.method.style.crud.delete.EntityWithoutPrimaryKeyDeleteBadDao
import test.java.failure.core.method.style.crud.delete.ParametersNotMatchDeleteBadDao

/**
 *
 */
class DeleteTest : BaseTest() {
    @Test
    fun entityWithoutPrimaryKey() {
        compileFailure<EntityWithoutPrimaryKeyDeleteBadDao> {
            assertErrorMessageEquals("The delete method requires that the Dao.crudEntity has primary key")
        }
    }

    @Test
    fun `parameter types does not match primary key`() {
        compileFailure<ParametersNotMatchDeleteBadDao> {
            assertErrorMessageEquals("Missing parameter of type java.lang.Long")
        }
    }
}