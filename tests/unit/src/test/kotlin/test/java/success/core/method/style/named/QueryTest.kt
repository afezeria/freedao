package test.java.success.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.named.prefix.*
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class QueryTest : BaseTest() {
    @Test
    fun findByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<FindByNameDao>()
        val list = impl.findByName("a")
        assert(list.size == 1)
    }

    @Test
    fun findOneById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<FindOneByIdDao>()
        val entity = impl.findOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryOneById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<QueryOneByIdDao>()
        val entity = impl.queryOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<QueryByNameDao>()
        val list = impl.queryByName("a")
        assert(list.size == 1)
    }

    @Test
    fun selectOneById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<SelectOneByIdDao>()
        val entity = impl.selectOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun selectByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<SelectByNameDao>()
        val list = impl.selectByName("a")
        assert(list.size == 1)
    }

    @Test
    fun dtoQueryByName() {
        initData(Person(1, "a"), Person(2, "b"), Person(3, "a"))
        val impl = getJavaDaoInstance<DtoQueryByNameDao>()
        val list = impl.dtoQueryByName("a")
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 3))
        list.forEach {
            it.apply {
                assert(name == "a")
                assert(fieldNotInEntity == null)
                assert(nickName == null)
            }
        }
    }

    @Test
    fun dtoExtendEntityResultTypeHandlerQuery() {
        initData(Person(1, "a", age = 10))
        val impl = getJavaDaoInstance<DtoQueryExtendEntityResultTypeHandlerDao>()
        val list = impl.dtoQueryByName("a")
        assert(list.size == 1)
        assertContentEquals(list.map { it.stringAge }, listOf("10"))
    }

    @Test
    fun dtoQueryOneByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<DtoQueryOneByNameDao>()
        val dto = impl.dtoQueryOneByName("a")
        assert(dto.name == "a")
        assert(dto.id == 1L)
    }
}