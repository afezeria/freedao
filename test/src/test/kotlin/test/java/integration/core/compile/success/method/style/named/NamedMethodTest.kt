package test.java.integration.core.compile.success.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class NamedMethodTest : BaseTest() {
    @Test
    fun `query by id and pass in irrelevant parameters`() {
        initData(Person(2, "b"), Person(1, "a"))
        val impl =
            getJavaDaoInstance<QueryByIdAndPassRuntimeContext>()
        val list = impl.queryById(mapOf(), 1)
        assert(list.size == 1)
        assert(list[0].name == "a")
    }
}