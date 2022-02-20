package test.java.failure

import org.junit.Test
import test.BaseTest
import test.errorMessages
import test.java.failure.dao.AnnotateClassWithDaoBadDao
import test.java.failure.dao.EntityWithoutTableAnnotationBadDao

/**
 *
 * @author afezeria
 */
class DaoTest : BaseTest() {
    @Test
    fun `error, annotate annotation class with Dao`() {
        compileFailure<AnnotateClassWithDaoBadDao> {
            assert(
                errorMessages.any { it == "Dao must be top level interface" }
            )
        }
    }

    @Test
    fun `error, entity without table annotation`() {
        compileFailure<EntityWithoutTableAnnotationBadDao> {
            assert(
                errorMessages.any { it == "The value of Dao.crudEntity must be annotated with table" }
            )
        }
    }
}