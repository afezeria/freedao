package test.java.integration.core.compile.failure.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 */
class InsertTest : BaseTest() {

    @Test
    fun `parameter type is different from the value of crudEntity `() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.insert.ParameterNotMatchInsertBadDao> {
            assertErrorMessageEquals("Missing parameter of type ${Person::class.qualifiedName}")
        }
    }

    @Test
    fun `insert, crudEntity has not insertable property `() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.insert.EntityHasNoInsertablePropertyInsertBadDao> {
            assertErrorMessageEquals("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
        }
    }

    @Test
    fun `insertNonNullFields, crudEntity has not insertable property `() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.insert.EntityHasNoInsertablePropertyInsertNonNullFieldBadDao> {
            assertErrorMessageEquals("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
        }
    }
}