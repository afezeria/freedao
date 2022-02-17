package test.java.failure

import org.junit.Test
import test.BaseTest
import test.errorMessages
import test.java.failure.dao.AnnotateClassWithBadDao
import test.java.failure.dao.EntityWithoutTableAnnotationBadDao

/**
 *
 * @author afezeria
 */
class DaoTest : BaseTest() {
    @Test
    fun annotateClassWithError() {
        compileFailure<AnnotateClassWithBadDao>{
            assert(
                errorMessages.any { it == "Dao must be top level interface" }
            )
        }
    }

    @Test
    fun entityWithoutTableAnnotationError() {
        compileFailure<EntityWithoutTableAnnotationBadDao>{
            assert(
                errorMessages.any { it == "The value of Dao.crudEntity must be annotated with table" }
            )
        }
    }
}