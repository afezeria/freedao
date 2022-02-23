package test.java.success.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.named.prefix.FindOneByIdDao
import test.java.success.core.method.style.named.prefix.QueryByNameDao
import test.java.success.core.method.style.named.prefix.QueryOneByIdDao

/**
 *
 * @author afezeria
 */
class QueryTest : BaseTest() {
    @Test
    fun findById() {
        initData(Person(1, "a"), Person(2, "b"))
        val impl = getJavaDaoInstance<FindOneByIdDao>()
        val entity = impl.findOneById(1L)
        assert(entity.id == 1L)
    }

    @Test
    fun queryById() {
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
}