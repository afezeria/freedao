package test.java.jpa

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class QueryTest : BaseTest() {
    @Test
    fun findById() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))
        val impl = getJavaDaoInstance<PersonFindByIdDao>()
        val entity = impl.findOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryById() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))
        val impl = getJavaDaoInstance<PersonQueryByIdDao>()
        val entity = impl.queryOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryByName() {
        initTable("person", listOf(mapOf("name" to "a"), mapOf("name" to "a")))
        val impl = getJavaDaoInstance<PersonQueryByNameDao>()
        val list = impl.queryByName("a")
        assert(list.size == 2)
    }
}