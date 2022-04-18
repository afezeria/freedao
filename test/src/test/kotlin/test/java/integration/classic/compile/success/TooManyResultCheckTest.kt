package test.java.integration.classic.compile.success

import com.github.afezeria.freedao.TooManyResultException
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
        assertFailsWith<TooManyResultException> {
            impl.selectOneByName("a")
        }
    }

}