package test.java.named.prefix

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
        val impl = getJavaDaoInstance<FindOneByIdDao>()
        val entity = impl.findOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryById() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))
        val impl = getJavaDaoInstance<QueryOneByIdDao>()
        val entity = impl.queryOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryByName() {
        initTable("person", listOf(mapOf("name" to "a"), mapOf("name" to "a")))
        val impl = getJavaDaoInstance<QueryByNameDao>()
        val list = impl.queryByName("a")
        assert(list.size == 2)
    }
}