package test.java.integration.classic.runtime

import io.github.afezeria.freedao.classic.runtime.SqlHelper
import org.junit.Test
import test.BaseTest
import test.JoinEntityA
import test.Person

/**
 *
 * @author afezeria
 */
class JoinQueryTests : BaseTest() {
    @Test
    fun simple() {
        initData(Person(id = 1, name = "a"))
        initData(JoinEntityA(name = "a", pId = 1))
        val instance = getJavaDaoInstance<SimpleJoinEntityQuery>()
        val entity = SqlHelper.join {
            instance.list(null)
        }[0]
        assert(entity.pName == "a")
    }
}