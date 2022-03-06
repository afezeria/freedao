package test.java.failure.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.failure.core.method.style.crud.insert.EntityHasNoInsertablePropertyInsertBadDao
import test.java.failure.core.method.style.crud.insert.EntityHasNoInsertablePropertyInsertNonNullFieldBadDao
import test.java.failure.core.method.style.crud.insert.ParameterNotMatchInsertBadDao

/**
 *
 */
class InsertTest : BaseTest() {

    @Test
    fun `parameter type is different from the value of crudEntity `() {
        compileFailure<ParameterNotMatchInsertBadDao> {
            assertErrorMessageEquals("Missing parameter of type ${Person::class.qualifiedName}")
        }
    }

    @Test
    fun `insert, crudEntity has not insertable property `() {
        compileFailure<EntityHasNoInsertablePropertyInsertBadDao> {
            assertErrorMessageEquals("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
        }
    }

    @Test
    fun `insertNonNullField, crudEntity has not insertable property `() {
        compileFailure<EntityHasNoInsertablePropertyInsertNonNullFieldBadDao> {
            assertErrorMessageEquals("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
        }
    }
}