package test.java.success.core.method.style.named

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.method.style.named.cond.*
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class ConditionTest : BaseTest() {
    @Test
    fun `is`() {
        initData(Person(2, "b"), Person(1, "a"))
        val impl = getJavaDaoInstance<IsDao>()
        val list = impl.queryById(1)
        assert(list.size == 1)
        assert(list[0].name == "a")
    }

    @Test
    fun between() {
        initData(Person(2, "b"), Person(5, "a"))
        val impl = getJavaDaoInstance<BetweenDao>()
        val list = impl.queryByIdBetween(1, 3)
        assert(list.size == 1)
        assert(list[0].id == 2L)
    }

    @Test
    fun `false`() {
        initData(Person(1, "b", active = true), Person(5, "a", active = false))
        val impl = getJavaDaoInstance<FalseDao>()
        val list = impl.queryByActiveFalse()
        assert(list.size == 1)
        assert(list[0].id == 5L)
    }

    @Test
    fun `true`() {
        initData(Person(1, "b", active = true), Person(5, "a", active = false))
        val impl = getJavaDaoInstance<TrueDao>()
        val list = impl.queryByActiveTrue()
        assert(list.size == 1)
        assert(list[0].id == 1L)
    }

    @Test
    fun greaterThan() {
        initData(Person(1, "b", active = true), Person(5, "a", active = false))
        val impl = getJavaDaoInstance<GreaterThanDao>()
        val list = impl.queryByIdGreaterThan(1)
        assert(list.size == 1)
        assert(list[0].id == 5L)
    }

    @Test
    fun greaterThanEqual() {
        initData(
            Person(1, "b"),
            Person(5, "a"),
            Person(8, "a")
        )
        val impl = getJavaDaoInstance<GreaterThanEqualDao>()
        val list = impl.queryByIdGreaterThanEqual(5)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(5, 8))
    }

    @Test
    fun lessThan() {
        initData(Person(1, "b", active = true), Person(5, "a", active = false))
        val impl = getJavaDaoInstance<LessThanDao>()
        val list = impl.queryByIdLessThan(5)
        assert(list.size == 1)
        assert(list[0].id == 1L)
    }

    @Test
    fun lessThanEqual() {
        initData(
            Person(1, "b"),
            Person(5, "a"),
            Person(8, "a")
        )
        val impl = getJavaDaoInstance<LessThanEqualDao>()
        val list = impl.queryByIdLessThanEqual(5)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 5))
    }


    @Test
    fun `in`() {
        initData(
            Person(1, "b", active = true),
            Person(5, "a", active = false),
            Person(6, "a", active = false),
        )
        val impl = getJavaDaoInstance<InDao>()
        var list = impl.queryByIdIn(listOf(1, 6))
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 6))

        list = impl.findByIdIn(setOf(1, 6))
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 6))
    }

    @Test
    fun notIn() {
        initData(
            Person(1, "b", active = true),
            Person(5, "a", active = false),
            Person(6, "a", active = false),
        )
        val impl = getJavaDaoInstance<NotInDao>()
        val list = impl.queryByIdNotIn(listOf(1, 6))
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(5))
    }

    @Test
    fun isNull() {
        initDataWithNullValue(
            Person(1, "b"),
            Person(5, null),
        )
        val impl = getJavaDaoInstance<IsNullDao>()
        val list = impl.queryByNameIsNull()
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(5))
    }

    @Test
    fun notNull() {
        initDataWithNullValue(
            Person(1, "b"),
            Person(5, null),
        )
        val impl = getJavaDaoInstance<NotNullDao>()
        val list = impl.queryByNameNotNull()
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun like() {
        initDataWithNullValue(
            Person(1, "b"),
            Person(5, "a"),
            Person(6, "ab"),
        )
        val impl = getJavaDaoInstance<LikeDao>()
        val list = impl.queryByNameLike("a%")
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(5, 6))
    }

    @Test
    fun notLike() {
        initDataWithNullValue(
            Person(1, "b"),
            Person(5, "a"),
            Person(6, "ab"),
        )
        val impl = getJavaDaoInstance<NotLikeDao>()
        val list = impl.queryByNameNotLike("a%")
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun not() {
        initDataWithNullValue(
            Person(1, "b"),
            Person(5, "a"),
            Person(6, "a"),
        )
        val impl = getJavaDaoInstance<NotDao>()
        val list = impl.queryByNameNot("a")
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun and() {
        initDataWithNullValue(
            Person(1, "a"),
            Person(5, "a"),
        )
        val impl = getJavaDaoInstance<AndDao>()
        val list = impl.queryByIdAndName(1, "a")
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun or() {
        initDataWithNullValue(
            Person(1, "a"),
            Person(5, "a"),
            Person(7, "b"),
        )
        val impl = getJavaDaoInstance<OrDao>()
        val list = impl.queryByIdOrName(1, "a")
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 5))
    }
}