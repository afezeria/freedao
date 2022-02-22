package test.java.classic

import org.junit.Test
import test.BaseTest
import test.Person
import test.PersonAnyId
import test.PersonStringId
import java.time.LocalDateTime
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ClassicTest : BaseTest() {
    @Test
    fun batchInsert() {
        initData<Person>()
        val list = mutableListOf(Person(null, "a"), Person(null, "b"))
        val impl = getJavaDaoInstance<BatchInsertDao>()
        val count = impl.batchInsert(list)
        assert(count == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `auto fill with type handler`() {
        initData<Person>()
        val list = mutableListOf(PersonStringId(null, "a"), PersonStringId(null, "b"))
        val impl = getJavaDaoInstance<AutoFillWithTypeHandlerDao>()
        val count = impl.batchInsert(list)
        assert(count == 2)
        assertContentEquals(list.map { it.id }, listOf("1", "2"))
    }

    @Test
    fun `auto fill with object id`() {
        initData<Person>()
        val list = mutableListOf(PersonAnyId(null, "a"), PersonAnyId(null, "b"))
        val impl = getJavaDaoInstance<AutoFillWithObjectIdDao>()
        val count = impl.batchInsert(list)
        assert(count == 2)
        assertContentEquals(list.map { it.id }, listOf(1L, 2L))
    }

    @Test
    fun `return single map with result mapping`() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<ReturnSingleMapWithResultMappingNoTypeHandlerDao>()
        val map = impl.query(1)
        assert(map["id"] == 1L)
    }

    @Test
    fun `return single map with result mapping 2`() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<ReturnSingleStringStringMapWithResultMappingNoTypeHandlerDao>()
        val map = impl.query(1)
        assert(map["name"] == "a")
    }

    @Test
    fun `return single map without result mapping`() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<ReturnSingleStringStringMapWithoutResultMappingDao>()
        val map = impl.query(1)
        assert(map["name"] == "a")
    }

    @Test
    fun `return map`() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<ReturnMapWithTypeHandlerDao>()
        val list = impl.all()
        assert(list.size == 2)
        assertContentEquals(list.map { it["id"] }, listOf(1L, 2L))
        assertContentEquals(list.map { it["name"] }, listOf('a', 'b'))
    }

    @Test
    fun `query when constructor for entity with parameters`() {
        val time = LocalDateTime.of(2000, 1, 1, 0, 0)
        initData(
            Person(1, "a", true, time, 1, "hello"),
            Person(2, "b")
        )
        val impl = getJavaDaoInstance<ReturnEntityWithConstructorDao>()
        val entity = impl.query(1)
        entity.apply {
            assert(id == 1L)
            assert(name == "a")
            assert(active == true)
            assert(whenCreated == time)
            assert(alias == "hello")
            assert(age == 1)
        }
    }

    @Test
    fun `query with custom mapping`() {
        val time = LocalDateTime.of(2000, 1, 1, 0, 0)
        initData(
            Person(1, "a", true, time, 1, "hello"),
            Person(2, "b")
        )
        val impl = getJavaDaoInstance<QueryEntityWithCustomMappingDao>()
        val entity = impl.query(1)
        entity.apply {
            assert(id == 1L)
            assert(name == "hello")
            assert(active == true)
            assert(whenCreated == time)
            assert(alias == "hello")
            assert(age == 1)
        }

    }
}