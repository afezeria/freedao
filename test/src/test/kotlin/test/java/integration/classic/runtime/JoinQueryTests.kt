package test.java.integration.classic.runtime

import io.github.afezeria.freedao.classic.runtime.SqlHelper
import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class JoinQueryTests : BaseTest() {
    @Test
    fun simple() {
        initData(Person(id = 1, name = "a"))
        initData(JoinEntityA().apply {
            name = "a"
            personId = 1
        })
        val instance = getJavaDaoInstance<SimpleJoinEntityQueryDao>()
        val entity = SqlHelper.join {
            instance.list(null)
        }[0]
        assert(entity.personName == "a")
    }
}