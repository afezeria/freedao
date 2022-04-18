package test.java.integration.core.compile.success.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.PersonType
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class QueryTest : BaseTest() {
    @Test
    fun findByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.FindByNameDao>()
        val list = impl.findByName("a")
        assert(list.size == 1)
    }

    @Test
    fun findOneById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.FindOneByIdDao>()
        val entity = impl.findOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryOneById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.QueryOneByIdDao>()
        val entity = impl.queryOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.QueryByNameDao>()
        val list = impl.queryByName("a")
        assert(list.size == 1)
    }

    @Test
    fun queryWithParameterTypeHandler() {
        initData<Person>()
        initTable(
            "person",
            listOf(
                mapOf("id" to 1, "type" to PersonType.TEACHER.name),
                mapOf("id" to 2, "type" to PersonType.STUDENT.name),
            )
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.QueryByWithParameterTypeHandlerDao>()
        val list = impl.queryByType(PersonType.TEACHER)
        assert(list.size == 1)
        assert(list[0].type == PersonType.TEACHER)
    }

    @Test
    fun selectOneById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.SelectOneByIdDao>()
        val entity = impl.selectOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun selectByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.SelectByNameDao>()
        val list = impl.selectByName("a")
        assert(list.size == 1)
    }

    @Test
    fun dtoQueryByName() {
        initData(Person(1, "a"), Person(2, "b"), Person(3, "a"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.DtoQueryByNameDao>()
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
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.DtoQueryExtendEntityResultTypeHandlerDao>()
        val list = impl.dtoQueryByName("a")
        assert(list.size == 1)
        assertContentEquals(list.map { it.stringAge }, listOf("10"))
    }

    @Test
    fun dtoQueryOneByName() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.prefix.DtoQueryOneByNameDao>()
        val dto = impl.dtoQueryOneByName("a")
        assert(dto.name == "a")
        assert(dto.id == 1L)
    }
}