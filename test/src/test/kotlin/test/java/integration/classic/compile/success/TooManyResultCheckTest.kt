package test.java.integration.classic.compile.success

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertFailsWith

/**
 *
 * @author afezeria
 */
class TooManyResultCheckTest : BaseTest() {
    @Test
    fun selectOneById() {
        initData(Person(1, "a"), Person(2, "a"))
        val impl = getJavaDaoInstance<TooManyResultCheckDao>()
        assertFailsWith<io.github.afezeria.freedao.TooManyResultException> {
            impl.selectOneByName("a")
        }
    }

}