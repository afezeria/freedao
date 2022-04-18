package test.java.integration.core.compile.success.method.resulthelper

import org.junit.Test
import test.BaseTest
import test.Person
import java.util.*
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ResultHelperTest : BaseTest() {
    @Test
    fun `query return single column`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnSingleColumnDao>()
        val list = impl.query()
        assert(list.size == 2)
        assertContentEquals(list, listOf(1L, 2L))
    }

    @Test
    fun `query with override constructor parameter mapping`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryWithOverrideParameterMappingDao>()
        val list = impl.query()
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list of HashMap`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnHashMapListDao>()
        val all = impl.query()
        assert(all.size == 2)
        assertContentEquals(all.map { it["id"] }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list of map`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnMapListDao>()
        val all = impl.query()
        assert(all.size == 2)
        assertContentEquals(all.map { it["id"] }, listOf(1L, 2L))
    }

    @Test
    fun `query method return list without type argument`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnListWithoutTypeArgumentDao>()
        val all = impl.query()
        assert(all.size == 2)
        assert(all[0] is Long)
//        assertContentEquals(all.map { it.id }, listOf(1, 3))
    }

    @Test
    fun `query method return set`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
            Person(3, "b"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnSetDao>()
        val all = impl.list(null)
        assert(all is HashSet)
        assert(all.size == 2)
        assertContentEquals(all.map { it.id }, listOf(1, 3))
    }

    @Test
    fun `query method return collection`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnCollectionDao>()
        val all = impl.list(null)
        assert(all is ArrayList)
        assert(all.size == 2)
        assertContentEquals(all.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `query method return non abstract collection`() {
        initData(
            Person(1, "a"),
            Person(2, "a"),
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.method.resulthelper.QueryReturnLinkedListDao>()
        val all = impl.list(null)
        assert(all is LinkedList)
        assert(all.size == 2)
        assertContentEquals(all.map { it.id }, listOf(1, 2))
    }

}