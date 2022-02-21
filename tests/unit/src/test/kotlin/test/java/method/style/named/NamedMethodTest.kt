package test.java.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages

/**
 *
 * @author afezeria
 */
class NamedMethodTest : BaseTest() {
    @Test
    fun `query by id and pass in irrelevant parameters`() {
        initData(Person(2, "b"), Person(1, "a"))
        val impl = getJavaDaoInstance<QueryByIdAndPassRuntimeContext>()
        val list = impl.queryById(mapOf(), 1)
        assert(list.size == 1)
        assert(list[0].name == "a")
    }

    @Test
    fun `error, property in the order clause that are not found in the entity`() {
        compileFailure<MissingOrderPropertyBadDao> {
            assert(
                errorMessages.contains("missing order property ${Person::class.qualifiedName}.nonexistentProperty")
            )
        }
    }

    @Test
    fun `error, property in the query condition that are not found in the entity`() {
        compileFailure<MissingConditionPropertyBadDao> {
            assert(
                errorMessages.contains("missing condition property ${Person::class.qualifiedName}.nonexistentProperty")
            )
        }
    }

    @Test
    fun `error, not specify crud entity`() {
        compileFailure<NotSpecifyCrudEntityBadDao> {
            assert(
                errorMessages.contains("Method queryById requires Dao.crudEntity to be specified")
            )
        }
    }

    @Test
    fun `error, missing parameter`() {
        compileFailure<MissingParameterBaoDao> {
            assert(
                errorMessages.contains("Missing java.lang.Long parameter")
            )
        }
    }

    @Test
    fun `error, parameter type mismatch`() {
        compileFailure<ParameterTypeMismatchBadDao> {
            assert(
                errorMessages.contains("Parameter mismatch, the 2th parameter type should be java.lang.String")
            )
        }
    }
}