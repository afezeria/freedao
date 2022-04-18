package test.java.integration.core.compile.failure.template

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class TemplateHandlerTest : BaseTest() {
    @Test
    fun `duplicate property declared`() {
        compileFailure<test.java.integration.core.compile.failure.template.template.DuplicatePropertyDeclaredBadDao> {
            assertErrorMessageEquals("Property 'i' already exists")
        }
    }

    @Test
    fun `property is not a map`() {
        compileFailure<test.java.integration.core.compile.failure.template.template.PropertyIsNotMapBadDao> {
            assertErrorMessageEquals("id is not a map")
        }
    }

    @Test
    fun `property is not a list`() {
        compileFailure<test.java.integration.core.compile.failure.template.template.PropertyIsNotListBadDao> {
            assertErrorMessageEquals("id is not a list")
        }
    }

    @Test
    fun `missing property`() {
        compileFailure<test.java.integration.core.compile.failure.template.template.MissingPropertyBadDao> {
            assertErrorMessageEquals("error expr:person.company, missing property:test.Person.company")
        }
    }

    @Test
    fun `expr type cast error`() {
        compileFailure<test.java.integration.core.compile.failure.template.template.ExprTypeCastErrorBaoDao> {
            assertErrorMessageEquals("a.b is of type java.lang.String cannot assignable to java.lang.Integer")
        }
    }

}