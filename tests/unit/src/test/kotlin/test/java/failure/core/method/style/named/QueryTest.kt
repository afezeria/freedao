package test.java.failure.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.failure.core.method.style.named.prefix.QueryNotReturnCollectionBadDao
import test.java.failure.core.method.style.named.prefix.QueryNotReturnEntityCollectionBadDao
import test.java.failure.core.method.style.named.prefix.QueryOneNotReturnEntityBadDao

/**
 *
 * @author afezeria
 */
class QueryTest : BaseTest() {
    @Test
    fun `return type of the query method is not collection`() {
        compileFailure<QueryNotReturnCollectionBadDao> {
            assertErrorMessageEquals("The return type of method must be a collection")
        }
    }

    @Test
    fun `return type of method query is not collection of crudEntity`() {
        compileFailure<QueryNotReturnEntityCollectionBadDao> {
            assertErrorMessageEquals("The element type of the return type must be a ${Person::class.qualifiedName}")
        }
    }

    @Test
    fun `return type of method queryOne is not crudEntity`() {
        compileFailure<QueryOneNotReturnEntityBadDao> {
            assertErrorMessageEquals("The return type of method must be ${Person::class.qualifiedName}")
        }
    }
}