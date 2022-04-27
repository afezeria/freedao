package test.java.integration.core.compile.failure.dao

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class DaoTest : BaseTest() {
    private val invalidCrudEntityMsg =
        "The class that are arguments to Dao.crudEntity must be custom java bean and annotated by io.github.afezeria.freedao.annotation.Table"

    @Test
    fun `error, annotate annotation class with Dao`() {
        compileFailure<AnnotateClassWithDaoBadDao> {
            assertErrorMessageEquals("Dao must be top level interface")
        }
    }

    @Test
    fun `entity without table annotation`() {
        compileFailure<EntityWithoutTableAnnotationBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun entityWithoutProperty() {
        compileFailure<EntityNoPropertyBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun primitiveTypeAsCrudEntity() {
        compileFailure<PrimitiveTypeAsCrudEntityBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun voidTypeAsCrudEntity() {
        compileFailure<VoidTypeAsCrudEntityBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityExtendList() {
        compileFailure<EntityExtendListBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityExtendMap() {
        compileFailure<EntityExtendMapBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityIsObject() {
        compileFailure<EntityIsObjectBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityIsString() {
        compileFailure<EntityIsStringBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }
}