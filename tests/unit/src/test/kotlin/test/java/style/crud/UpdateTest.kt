package test.java.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.errorMessages
import test.java.style.crud.update.EntityHasNoUpdatePropertyUpdateBadDao
import test.java.style.crud.update.EntityWithoutPrimaryKeyUpdateBadDao
import test.java.style.crud.update.PersonUpdateDao
import test.java.style.crud.update.PersonUpdateSelectiveDao

/**
 *
 */
class UpdateTest : BaseTest() {

    @Test
    fun `update all field`() {
        initData(Person(1, "a"))

        env.find("person")[0].let {
            assert(it["when_created"] != null)
        }

        val impl = getJavaDaoInstance<PersonUpdateDao>()
        val entity = Person(1, "b")

        val update = impl.update(entity)
        assert(update == 1)
        env.find("person")[0].let {
            assert(it["name"] == "b")
            assert(it["when_created"] == null)
        }
    }

    @Test
    fun `update non-null field`() {
        initData(Person(1, "a"))

        env.find("person")[0].let {
            assert(it["when_created"] != null)
        }

        val impl = getJavaDaoInstance<PersonUpdateSelectiveDao>()
        val entity = Person(1, "b")

        val update = impl.updateSelective(entity)
        assert(update == 1)
        env.find("person")[0].let {
            assert(it["name"] == "b")
            assert(it["when_created"] != null)
        }
    }

    @Test
    fun `error, entity without primary key`() {
        compileFailure<EntityWithoutPrimaryKeyUpdateBadDao> {
            assert(
                errorMessages.contains("The update method requires that the class specified by Dao.crudEntity must have primary key")
            )
        }
    }

    @Test
    fun `error, crudEntity has not update property `() {
        compileFailure<EntityHasNoUpdatePropertyUpdateBadDao> {
            assert(
                errorMessages.contains("The entity class specified by Dao.crudEntity has no property that can be used for update")
            )
        }
    }

}