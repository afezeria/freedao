package test.java.success.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.crud.insert.PersonInsertDao
import test.java.success.core.method.style.crud.insert.PersonInsertNonNullFieldDao
import test.java.success.core.method.style.crud.insert.ReturnLongPersonInsertDao

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
        val impl = getJavaDaoInstance<PersonInsertNonNullFieldDao>()
        val entity = Person(name = "a")
        val updateCount = impl.insertNonNullField(entity)
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

}