package test.java.integration.core.compile.success.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class OrderTest : BaseTest() {
    @Test
    fun onlyOrderBy() {
        initData(Person(2, "a"), Person(3, "b"), Person(1, "a"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.order.OnlyOrderByDao>()
        val list = impl.queryByOrderByNameDescIdAsc()
        assertContentEquals(list.map { it.id }, listOf(3, 1, 2))
    }

    @Test
    fun orderByIdAsc() {
        initData(Person(2, "a"), Person(1, "a"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.order.QueryWithSingleColumn>()
        val list = impl.queryByNameOrderByIdAsc("a")
        val sorted = list.sortedBy { it.id }
        assertContentEquals(list, sorted)
    }

    @Test
    fun orderByIdDesc() {
        initData(Person(2, "a"), Person(1, "a"))
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.order.QueryWithSingleColumn>()
        val list = impl.queryByNameOrderByIdDesc("a")
        val sorted = list.sortedBy { it.id }.reversed()
        assertContentEquals(list, sorted)
    }

    @Test
    fun orderByNameAscIdAsc() {
        initData(
            Person(1, "b"),
            Person(2, "a"),
            Person(3, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.order.OrderWithMultipleColumnDao>()
        val list = impl.queryByWhenCreatedNotNullOrderByNameAscIdAsc()
        assertContentEquals(list.map { it.id }, listOf(2, 3, 1))
    }

    @Test
    fun orderByNameAscIdDesc() {
        initData(
            Person(1, "b"),
            Person(2, "a"),
            Person(3, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.style.named.order.OrderWithMultipleColumnDao>()
        val list = impl.queryByIdNotNullOrderByNameAscIdDesc()
        assertContentEquals(list.map { it.id }, listOf(3, 2, 1))
    }
}