package test.java.integration.core.compile.success.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.integration.core.compile.success.method.style.crud.update.PersonUpdateDao
import test.java.integration.core.compile.success.method.style.crud.update.PersonUpdateNonNullFieldDao

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

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.update.PersonUpdateDao>()
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

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.crud.update.PersonUpdateNonNullFieldDao>()
        val entity = Person(1, "b")

        val update = impl.updateNonNullFields(entity)
        assert(update == 1)
        env.find("person")[0].let {
            assert(it["name"] == "b")
            assert(it["when_created"] != null)
        }
    }
}