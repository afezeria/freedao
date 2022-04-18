package test.java.integration.core.compile.success.method.style.xml.complex

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class TestExprTest : BaseTest() {
    @Test
    fun test1() {
        initData(
            Person(1, "a", active = true),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.xml.complex.TestExpr1Dao>()
        val h = mutableListOf(mutableMapOf("abc" to test.java.integration.core.compile.success.method.style.xml.complex.TestExpr1Dao.D().setC(mutableListOf(1, 2, 3))))
        val list1 = impl.query(
            test.java.integration.core.compile.success.method.style.xml.complex.TestExpr1Dao.A().setG(true),
            true,
            2L,
            true,
            -2.0,
            null,
            false,
            h,
        )
        assert(list1.size == 0)

        val list2 = impl.query(
            test.java.integration.core.compile.success.method.style.xml.complex.TestExpr1Dao.A().setG(true),
            true,
            2L,
            true,
            -2.0,
            Any(),
            false,
            h,
        )
        assert(list2.size == 1)
    }
}