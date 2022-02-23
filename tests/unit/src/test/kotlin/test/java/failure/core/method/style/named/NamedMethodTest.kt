package test.java.failure.core.method.style.named

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
        compileFailure<MissingOrderPropertyBadDao> {
            assertErrorMessageEquals("missing order property ${Person::class.qualifiedName}.nonexistentProperty")
        }
    }

    @Test
    fun `property in the query condition that are not found in the entity`() {
        compileFailure<MissingConditionPropertyBadDao> {
            assertErrorMessageEquals("missing condition property ${Person::class.qualifiedName}.nonexistentProperty")
        }
    }

    @Test
    fun `not specify crud entity`() {
        compileFailure<NotSpecifyCrudEntityBadDao> {
            assertErrorMessageEquals("Method queryById requires Dao.crudEntity to be specified")
        }
    }

    @Test
    fun `missing parameter`() {
        compileFailure<MissingParameterBaoDao> {
            assertErrorMessageEquals("Missing java.lang.Long parameter")
        }
    }

    @Test
    fun `parameter type mismatch`() {
        compileFailure<ParameterTypeMismatchBadDao> {
            assertErrorMessageEquals("Parameter mismatch, the 2th parameter type should be java.lang.String")
        }
    }
}