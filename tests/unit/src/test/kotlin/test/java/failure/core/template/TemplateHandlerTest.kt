package test.java.failure.core.template

import org.junit.Test
import test.BaseTest
import test.java.failure.core.template.template.*

/**
 *
 * @author afezeria
 */
class TemplateHandlerTest : BaseTest() {
    @Test
    fun `duplicate property declared`() {
        compileFailure<DuplicatePropertyDeclaredBadDao> {
            assertErrorMessageEquals("Property 'i' already exists")
        }
    }

    @Test
    fun `property is not a map`() {
        compileFailure<PropertyIsNotMapBadDao> {
            assertErrorMessageEquals("id is not a map")
        }
    }

    @Test
    fun `property is not a list`() {
        compileFailure<PropertyIsNotListBadDao> {
            assertErrorMessageEquals("id is not a list")
        }
    }

    @Test
    fun `missing property`() {
        compileFailure<MissingPropertyBadDao> {
            assertErrorMessageEquals("error expr:person.company, missing property:test.Person.company.")
        }
    }

    @Test
    fun `expr type cast error`() {
        compileFailure<ExprTypeCastErrorBaoDao> {
            assertErrorMessageEquals("a.b is of type java.lang.String cannot assignable to java.lang.Integer")
        }
    }

}