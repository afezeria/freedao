package test.java.method.result

import org.junit.Test
import test.BaseTest
import test.Person
import test.StringResultTypeHandler
import test.errorMessages
import java.util.*
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ResultHelperTest : BaseTest() {
    @Test
    fun `query return single column`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnSingleColumnDao>()
        val list = impl.query()
        assert(list.size == 2)
        assertContentEquals(list, listOf(1L, 2L))
    }

    @Test
    fun `query with override constructor parameter mapping`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryWithOverrideParameterMappingDao>()
        val list = impl.query()
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list of HashMap`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnHashMapListDao>()
        val all = impl.query()
        assert(all.size == 2)
        assertContentEquals(all.map { it["id"] }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list of map`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnMapListDao>()
        val all = impl.query()
        assert(all.size == 2)
        assertContentEquals(all.map { it["id"] }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list of map without type argument`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnMapListWithoutTypeArgumentDao>()
        val all = impl.query()
        assert(all.size == 2)
        assertContentEquals(all.map { it["id"] }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list without type argument`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnListWithoutTypeArgumentDao>()
        val all = impl.query()
        assert(all.size == 2)
        assert(all[0] is Long)
//        assertContentEquals(all.map { it.id }, listOf(1, 3))
    }

    @Test
    fun `query method return set`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
            Person(3, "b"),
        )
        val impl = getJavaDaoInstance<QueryReturnSetDao>()
        val all = impl.all()
        assert(all is HashSet)
        assert(all.size == 2)
        assertContentEquals(all.map { it.id }, listOf(1, 3))
    }

    @Test
    fun `query method return collection`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnCollectionDao>()
        val all = impl.all()
        assert(all is ArrayList)
        assert(all.size == 2)
        assertContentEquals(all.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `query method return non abstract collection`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<QueryReturnLinkedListDao>()
        val all = impl.all()
        assert(all is LinkedList)
        assert(all.size == 2)
        assertContentEquals(all.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `error, return an abstract collection and is different from List or Set or Collection`() {
        compileFailure<QueryReturnConcurrentListBadDao> {
            assert(
                errorMessages.contains("Invalid return type")
            )
        }
    }

    @Test
    fun `error, return single row result and the type is abstract but not Map`() {
        compileFailure<QueryReturnNumberBadDao> {
            assert(
                errorMessages.contains("Invalid return type:java.lang.Number, the abstract type of single row result can only be Map")
            )
        }
    }

    @Test
    fun `error, return Integer Integer Map`() {
        compileFailure<QueryReturnIntIntMapBadDao> {
            assert(
                errorMessages.contains("Invalid type argument:java.lang.Integer, the key type must be String")
            )
        }
    }

    @Test
    fun `error, return Integer Integer HashMap`() {
        compileFailure<QueryReturnIntIntHashMapBadDao> {
            assert(
                errorMessages.contains("Invalid type argument:java.lang.Integer, the key type must be String")
            )
        }
    }

    @Test
    fun `error, return String Number Map`() {
        compileFailure<QueryReturnStringNumberMapBadDao> {
            assert(
                errorMessages.contains("Invalid type argument:java.lang.Number, the value type cannot be abstract")
            )
        }
    }

    @Test
    fun `error, return String Number HashMap`() {
        compileFailure<QueryReturnStringNumberHashMapBadDao> {
            assert(
                errorMessages.contains("Invalid type argument:java.lang.Number, the value type cannot be abstract")
            )
        }
    }

    @Test
    fun `error, return list list`() {
        compileFailure<QueryReturnListListBadDao> {
            assert(
                errorMessages.contains("Invalid type argument:java.util.ArrayList<test.Person>")
            )
        }
    }

    @Test
    fun `error, return void`() {
        compileFailure<QueryReturnVoidBadDao> {
            assert(
                errorMessages.contains("Invalid return type, cannot return void")
            )
        }
    }

    @Test
    fun `error, invalid result mapping`() {
        compileFailure<EmptyMappingBadDao> {
            assert(
                errorMessages.contains("Invalid result mapping, value cannot be empty when onlyCustomMapping is true")
            )
        }
    }

    @Test
    fun `error, entity without public constructor`() {
        compileFailure<EntityWithoutPublicConstructorBadDao> {
            assert(
                errorMessages.contains("Return type test.PersonWithoutPublicConstructor must have a public constructor")
            )
        }
    }

    @Test
    fun `error, entity parameter has no corresponding field`() {
        compileFailure<EntityParameterHasNoCorrespondingFieldBadDao> {
            assert(
                errorMessages.contains("Constructor parameter name must be the same as field name:test.PersonBad1.test")
            )
        }
    }

    @Test
    fun `error, invalid wrong type handler`() {
        compileFailure<WrongTypeHandlerBadDao> {
            assert(
                errorMessages.contains("Invalid ResultTypeHandler:java.lang.String, missing method:handle(Object.class)")
            )
        }
    }

    @Test
    fun `error, mapping target not found`() {
        compileFailure<MappingTargetNotFoundBadDao> {
            assert(
                errorMessages.contains("test.Person is missing the abc field")
            )
        }
    }

    @Test
    fun `error, result type handler and property type do not match`() {
        compileFailure<TypeHandlerNotMatchFieldBadDao> {
            assert(
                errorMessages.contains("${StringResultTypeHandler::class.qualifiedName} cannot handle id:java.lang.Long field")
            )
        }
    }

    @Test
    fun `error, result type handler and list item type do not match`() {
        compileFailure<TypeHandlerNotMatchListItemTypeBadDao> {
            assert(
                errorMessages.contains("${StringResultTypeHandler::class.qualifiedName} does not match java.lang.Long type")
            )
        }
    }
    @Test
    fun `error, result type handler and map value type not match`() {
        compileFailure<TypeHandlerNotMatchMapValueTypeBadDao> {
            assert(
                errorMessages.contains("${StringResultTypeHandler::class.qualifiedName} does not match java.lang.Long type")
            )
        }
    }
}