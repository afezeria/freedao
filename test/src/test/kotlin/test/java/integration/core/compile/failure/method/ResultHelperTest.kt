package test.java.integration.core.compile.failure.method

import org.junit.Test
import test.BaseTest
import test.StringResultTypeHandler

/**
 *
 * @author afezeria
 */
class ResultHelperTest : BaseTest() {
    @Test
    fun `return an abstract collection and is different from List or Set or Collection`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnConcurrentListBadDao> {
            assertErrorMessageEquals("Invalid return type")
        }
    }

    @Test
    fun `return single row result and the type is abstract but not Map`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnNumberBadDao> {
            assertErrorMessageEquals("Invalid return type:java.lang.Number, the abstract type of single row result can only be Map")
        }
    }

    @Test
    fun `return Integer Integer Map`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnIntIntMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Integer, the key type must be String")
        }
    }

    @Test
    fun `return Integer Integer HashMap`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnIntIntHashMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Integer, the key type must be String")
        }
    }

    @Test
    fun `return String Number Map`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnStringNumberMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Number, the value type cannot be abstract")
        }
    }

    @Test
    fun `return String Number HashMap`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnStringNumberHashMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Number, the value type cannot be abstract")
        }
    }

    @Test
    fun `return list list`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnListListBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.util.ArrayList<test.Person>")
        }
    }

    @Test
    fun `return void`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnVoidBadDao> {
            assertErrorMessageEquals("Invalid return type, cannot return void")
        }
    }

    @Test
    fun `invalid result mapping`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.EmptyMappingBadDao> {
            assertErrorMessageEquals("Invalid result mapping, value cannot be empty when onlyCustomMapping is true")
        }
    }

    @Test
    fun `entity without public constructor`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.EntityWithoutPublicConstructorBadDao> {
            assertErrorMessageEquals("Return type test.PersonWithoutPublicConstructor must have a public constructor")
        }
    }

    @Test
    fun `entity parameter has no corresponding field`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.EntityParameterHasNoCorrespondingFieldBadDao> {
            assertErrorMessageEquals("Constructor parameter name must be the same as field name:test.PersonBad1.test")
        }
    }

    @Test
    fun `invalid wrong type handler`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.WrongTypeHandlerBadDao> {
            assertErrorMessageEquals("Invalid ResultTypeHandler:java.lang.String, missing method:public static Object handleResult(Object.class)")
        }
    }

    @Test
    fun `mapping target not found`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.MappingTargetNotFoundBadDao> {
            assertErrorMessageEquals("test.Person is missing the abc field")
        }
    }

    @Test
    fun `result type handler and property type do not match`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.TypeHandlerNotMatchFieldBadDao> {
            assertErrorMessageEquals("${StringResultTypeHandler::class.qualifiedName} cannot handle id:java.lang.Long field")
        }
    }

    @Test
    fun `result type handler and list item type do not match`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.TypeHandlerNotMatchListItemTypeBadDao> {
            assertErrorMessageEquals("${StringResultTypeHandler::class.qualifiedName} does not match java.lang.Long type")
        }
    }

    @Test
    fun `result type handler and map value type not match`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.TypeHandlerNotMatchMapValueTypeBadDao> {
            assertErrorMessageEquals("${StringResultTypeHandler::class.qualifiedName} does not match java.lang.Long type")
        }
    }

    @Test
    fun `query method return list of map without type argument`() {
        compileFailure<test.java.integration.core.compile.failure.method.resulthelper.QueryReturnMapListWithoutTypeArgumentBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Object, the key type must be String")
        }
//        initData(
//            Person(1, "a"),
//            Person(2, "a"),
//        )
//        val impl = getJavaDaoInstance<>()
//        val all = impl.query()
//        assert(all.size == 2)
//        assertContentEquals(all.map { it["id"] }, listOf(1L, 2L))
    }


}