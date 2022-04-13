package test.java.failure.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.DtoNoCommonFieldWithPerson
import test.Person
import test.java.failure.core.method.style.named.prefix.*

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
    fun `return type of the dtoQuery method is not collection`() {
        compileFailure<DtoQueryNotReturnCollectionBadDao> {
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

    @Test
    fun `return type of method dtoQueryOne is not custom java bean`() {
        compileFailure<DtoQueryOneNotReturnCustomJavaBeanBadDao> {
            assertErrorMessageEquals("The return type of the return type must be a java bean")
        }
    }

    @Test
    fun `return type of method dtoQuery is not collection of java bean`() {
        compileFailure<DtoQueryNotReturnCustomJavaBeanBadDao> {
            assertErrorMessageEquals("The element type of the return type must be a java bean")
        }
    }

    @Test
    fun `no common fields between dto and entity`() {
        compileFailure<DtoQueryNoCommonFieldBadDao> {
            assertErrorMessageEquals("There are no fields in common between entity(${Person::class.qualifiedName}) and dto(${DtoNoCommonFieldWithPerson::class.qualifiedName})")
        }
    }

    @Test
    fun `missing sort keyword`() {
        compileFailure<MissingSortKeywordBadDao> {
            assertErrorMessageEquals("missing sort keyword asc or desc")
        }
    }
}