package test.java.integration.core.compile.failure.method.style.crud

import org.junit.Test
import test.BaseTest
import test.java.integration.core.compile.failure.method.style.crud.update.EntityHasNoUpdatePropertyUpdateBadDao
import test.java.integration.core.compile.failure.method.style.crud.update.EntityWithoutPrimaryKeyUpdateBadDao

/**
 *
 */
class UpdateTest : BaseTest() {

    @Test
    fun `entity without primary key`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.update.EntityWithoutPrimaryKeyUpdateBadDao> {
            assertErrorMessageEquals("The update method requires that the class specified by Dao.crudEntity must have primary key")
        }
    }

    @Test
    fun `crudEntity has not update property `() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.update.EntityHasNoUpdatePropertyUpdateBadDao> {
            assertErrorMessageEquals("The entity class specified by Dao.crudEntity has no property that can be used for update")
        }
    }

}