package test.java.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages
import test.java.style.crud.insert.*

/**
 *
 */
class InsertTest : BaseTest() {

    @Test
    fun `insert all fields`() {
        initData<Person>()
        val impl = getJavaDaoInstance<PersonInsertDao>()
        val entity = Person(name = "a")
        val updateCount = impl.insert(entity)
        assert(updateCount == 1)
        assert(entity.id != null)
        env.find("person", "id = ${entity.id}")[0].let {
            assert(it["when_created"] == null)
        }
    }

    @Test
    fun `insert non-null fields`() {
        initData<Person>()
        val impl = getJavaDaoInstance<PersonInsertSelectiveDao>()
        val entity = Person(name = "a")
        val updateCount = impl.insertSelective(entity)
        assert(updateCount == 1)
        assert(entity.id != null)
        env.find("person", "id = ${entity.id}")[0].let {
            assert(it["when_created"] != null)
        }
    }

    @Test
    fun `insert return long`() {
        initData<Person>()
        val impl = getJavaDaoInstance<ReturnLongPersonInsertDao>()
        val entity = Person(name = "a")
        val updateCount = impl.insert(entity)
        assert(updateCount == 1L)
        assert(entity.id != null)
        env.find("person", "id = ${entity.id}")[0].let {
            assert(it["when_created"] == null)
        }
    }

    @Test
    fun `error, parameter type is different from the value of crudEntity `() {
        compileFailure<ParameterNotMatchInsertBadDao> {
            assert(
                errorMessages.contains("Missing parameter of type ${Person::class.qualifiedName}")
            )
        }
    }

    @Test
    fun `error, insert, crudEntity has not insertable property `() {
        compileFailure<EntityHasNoInsertablePropertyInsertBadDao> {
            assert(
                errorMessages.contains("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
            )
        }
    }
    @Test
    fun `error, insertSelective, crudEntity has not insertable property `() {
        compileFailure<EntityHasNoInsertablePropertyInsertSelectiveBadDao> {
            assert(
                errorMessages.contains("The entity class specified by Dao.crudEntity has no property that can be used for insertion")
            )
        }
    }
}