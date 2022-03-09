package test.java.failure.core.method

import org.junit.Test
import test.BaseTest
import test.StringResultTypeHandler
import test.java.failure.core.method.resulthelper.*

/**
 *
 * @author afezeria
 */
class ResultHelperTest : BaseTest() {
    @Test
    fun `return an abstract collection and is different from List or Set or Collection`() {
        compileFailure<QueryReturnConcurrentListBadDao> {
            assertErrorMessageEquals("Invalid return type")
        }
    }

    @Test
    fun `return single row result and the type is abstract but not Map`() {
        compileFailure<QueryReturnNumberBadDao> {
            assertErrorMessageEquals("Invalid return type:java.lang.Number, the abstract type of single row result can only be Map")
        }
    }

    @Test
    fun `return Integer Integer Map`() {
        compileFailure<QueryReturnIntIntMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Integer, the key type must be String")
        }
    }

    @Test
    fun `return Integer Integer HashMap`() {
        compileFailure<QueryReturnIntIntHashMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Integer, the key type must be String")
        }
    }

    @Test
    fun `return String Number Map`() {
        compileFailure<QueryReturnStringNumberMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Number, the value type cannot be abstract")
        }
    }

    @Test
    fun `return String Number HashMap`() {
        compileFailure<QueryReturnStringNumberHashMapBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.lang.Number, the value type cannot be abstract")
        }
    }

    @Test
    fun `return list list`() {
        compileFailure<QueryReturnListListBadDao> {
            assertErrorMessageEquals("Invalid type argument:java.util.ArrayList<test.Person>")
        }
    }

    @Test
    fun `return void`() {
        compileFailure<QueryReturnVoidBadDao> {
            assertErrorMessageEquals("Invalid return type, cannot return void")
        }
    }

    @Test
    fun `invalid result mapping`() {
        compileFailure<EmptyMappingBadDao> {
            assertErrorMessageEquals("Invalid result mapping, value cannot be empty when onlyCustomMapping is true")
        }
    }

    @Test
    fun `entity without public constructor`() {
        compileFailure<EntityWithoutPublicConstructorBadDao> {
            assertErrorMessageEquals("Return type test.PersonWithoutPublicConstructor must have a public constructor")
        }
    }

    @Test
    fun `entity parameter has no corresponding field`() {
        compileFailure<EntityParameterHasNoCorrespondingFieldBadDao> {
            assertErrorMessageEquals("Constructor parameter name must be the same as field name:test.PersonBad1.test")
        }
    }

    @Test
    fun `invalid wrong type handler`() {
        compileFailure<WrongTypeHandlerBadDao> {
            assertErrorMessageEquals("Invalid ResultTypeHandler:java.lang.String, missing method:public static Object handleResult(Object.class)")
        }
    }

    @Test
    fun `mapping target not found`() {
        compileFailure<MappingTargetNotFoundBadDao> {
            assertErrorMessageEquals("test.Person is missing the abc field")
        }
    }

    @Test
    fun `result type handler and property type do not match`() {
        compileFailure<TypeHandlerNotMatchFieldBadDao> {
            assertErrorMessageEquals("${StringResultTypeHandler::class.qualifiedName} cannot handle id:java.lang.Long field")
        }
    }

    @Test
    fun `result type handler and list item type do not match`() {
        compileFailure<TypeHandlerNotMatchListItemTypeBadDao> {
            assertErrorMessageEquals("${StringResultTypeHandler::class.qualifiedName} does not match java.lang.Long type")
        }
    }

    @Test
    fun `result type handler and map value type not match`() {
        compileFailure<TypeHandlerNotMatchMapValueTypeBadDao> {
            assertErrorMessageEquals("${StringResultTypeHandler::class.qualifiedName} does not match java.lang.Long type")
        }
    }

}