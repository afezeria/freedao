package test.java.integration.core.compile.failure.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class NamedMethodTest : BaseTest() {
    @Test
    fun `property in the order clause that are not found in the entity`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.MissingOrderPropertyBadDao> {
            assertErrorMessageEquals("missing order property ${Person::class.qualifiedName}.nonexistentProperty")
        }
    }

    @Test
    fun `property in the query condition that are not found in the entity`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.MissingConditionPropertyBadDao> {
            assertErrorMessageEquals("missing condition property ${Person::class.qualifiedName}.nonexistentProperty")
        }
    }

    @Test
    fun `not specify crud entity`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.NotSpecifyCrudEntityBadDao> {
            assertErrorMessageEquals("Method queryById requires Dao.crudEntity to be specified")
        }
    }

    @Test
    fun `missing parameter`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.MissingParameterBaoDao> {
            assertErrorMessageEquals("Missing java.lang.Long parameter")
        }
    }

    @Test
    fun `parameter type mismatch`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.named.ParameterTypeMismatchBadDao> {
            assertErrorMessageEquals("Parameter mismatch, the 2th parameter type should be java.lang.String")
        }
    }
}