package test.java.integration.core.compile.failure.dao

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class DaoTest : BaseTest() {
    private val invalidCrudEntityMsg =
        "The class that are arguments to Dao.crudEntity must be custom java bean and annotated by com.github.afezeria.freedao.annotation.Table"

    @Test
    fun `error, annotate annotation class with Dao`() {
        compileFailure<test.java.integration.core.compile.failure.dao.AnnotateClassWithDaoBadDao> {
            assertErrorMessageEquals("Dao must be top level interface")
        }
    }

    @Test
    fun `entity without table annotation`() {
        compileFailure<test.java.integration.core.compile.failure.dao.EntityWithoutTableAnnotationBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun entityWithoutProperty() {
        compileFailure<test.java.integration.core.compile.failure.dao.EntityNoPropertyBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun primitiveTypeAsCrudEntity() {
        compileFailure<test.java.integration.core.compile.failure.dao.PrimitiveTypeAsCrudEntityBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun voidTypeAsCrudEntity() {
        compileFailure<test.java.integration.core.compile.failure.dao.VoidTypeAsCrudEntityBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityExtendList() {
        compileFailure<test.java.integration.core.compile.failure.dao.EntityExtendListBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityExtendMap() {
        compileFailure<test.java.integration.core.compile.failure.dao.EntityExtendMapBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityIsObject() {
        compileFailure<test.java.integration.core.compile.failure.dao.EntityIsObjectBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }

    @Test
    fun crudEntityIsString() {
        compileFailure<test.java.integration.core.compile.failure.dao.EntityIsStringBadDao> {
            assertErrorMessageEquals(invalidCrudEntityMsg)
        }
    }
}