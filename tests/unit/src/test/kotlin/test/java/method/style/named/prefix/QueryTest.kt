package test.java.method.style.named.prefix

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages

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

    @Test
    fun `error, return type of the query method is not collection`() {
        compileFailure<QueryNotReturnCollectionBadDao> {
            assert(
                errorMessages.contains("The return type of method must be a collection")
            )
        }
    }

    @Test
    fun `error, return type of method query is not collection of crudEntity`() {
        compileFailure<QueryNotReturnEntityCollectionBadDao> {
            assert(
                errorMessages.contains("The element type of the return type must be a ${Person::class.qualifiedName}")
            )
        }
    }

    @Test
    fun `error, return type of method queryOne is not crudEntity`() {
        compileFailure<QueryOneNotReturnEntityBadDao> {
            assert(
                errorMessages.contains("The return type of method must be ${Person::class.qualifiedName}")
            )
        }
    }
}