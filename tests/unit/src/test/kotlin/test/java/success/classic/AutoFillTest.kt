package test.java.success.classic

import org.junit.Test
import test.*
import test.java.success.classic.autofill.*
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class AutoFillTest : BaseTest() {
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
    fun `fill database generated key`() {
        initData(AutoFillEntity::class)
        val impl = getJavaDaoInstance<FillGeneratedKeysDao>()
        val entity = DbGeneratedKeyEntity(null, "a")
        val count = impl.insert(entity)
        assert(count == 1)
        assert(entity.id == 1L)
    }

    @Test
    fun `fill multiple property with database generated key`() {
        initData(AutoFillEntity::class)
        val impl = getJavaDaoInstance<FillMultiGeneratedKeysDao>()
        val entity = MultiDbGeneratedKeysEntity(null, "a")
        val count = impl.insert(entity)
        assert(count == 1)
        assert(entity.id == 1L)
        assert(entity.whenCreated != null)
    }

    @Test
    fun `custom id generator`() {
        initData(AutoFillEntity::class)
        NegativeLongIdGenerator.reset()
        val impl = getJavaDaoInstance<CustomIdGeneratorDao>()
        val entity = CustomIdGeneratorEntity(null, "a")
        val count = impl.insert(entity)
        assert(count == 1)
        assert(entity.id == -1L)
        val entity2 = CustomIdGeneratorEntity(null, "a")

        val count2 = impl.insert(entity2)
        assert(count2 == 1)
        assert(entity2.id == -2L)

    }

}