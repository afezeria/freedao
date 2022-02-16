package test.java.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
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

        println()
    }

}