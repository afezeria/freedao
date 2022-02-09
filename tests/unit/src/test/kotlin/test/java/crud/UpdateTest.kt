package test.java.crud

import org.junit.Test
import test.BaseTest
import test.java.crud.update.PersonUpdate
import test.java.crud.update.PersonUpdateDao
import test.java.crud.update.PersonUpdateSelective
import test.java.crud.update.PersonUpdateSelectiveDao

/**
 *
 */
class UpdateTest : BaseTest() {

    @Test
    fun `update all field`() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))

        env.find("person")[0].let {
            assert(it["create_date"] != null)
        }

        val impl = getJavaDaoInstance<PersonUpdateDao>()
        val entity = PersonUpdate().apply {
            id = 1
            name = "b"
        }
        val update = impl.update(entity)
        assert(update == 1)
        env.find("person")[0].let {
            assert(it["name"] == "b")
            assert(it["create_date"] == null)
        }
    }

    @Test
    fun `update non-null field`() {
        initTable("person", listOf(mapOf("id" to 1, "name" to "a")))

        env.find("person")[0].let {
            assert(it["create_date"] != null)
        }

        val impl = getJavaDaoInstance<PersonUpdateSelectiveDao>()
        val entity = PersonUpdateSelective().apply {
            id = 1
            name = "b"
        }
        val update = impl.updateSelective(entity)
        assert(update == 1)
        env.find("person")[0].let {
            assert(it["name"] == "b")
            assert(it["create_date"] != null)
        }

        println()
    }

}