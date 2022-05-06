package test.kotlin

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class KotlinTests : BaseTest() {
    @Test
    fun abc() {
        initData(
            Person(name = "a"),
            Person(name = "b"),
        )
        val dao = getKotlinDaoInstance<PersonDao>()
        val count = dao.count(null)
        assert(count == 2)

        println()

    }
}