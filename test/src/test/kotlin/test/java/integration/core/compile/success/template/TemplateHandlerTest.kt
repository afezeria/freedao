package test.java.integration.core.compile.success.template

import org.junit.Test
import test.BaseTest
import test.Person
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class TemplateHandlerTest : BaseTest() {
    @Test
    fun `access map without type argument`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.AccessMapDao>()
        val list = impl.query(mapOf("a" to 0))
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `access list without type argument`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.AccessListDao>()
        val list = impl.query(listOf(0, 0))
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `get list size`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.CompareListSizeDao>()
        val list = impl.query(emptyList())
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `get map size`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.CompareMapSizeDao>()
        val list = impl.query(emptyMap<Any, Any>())
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `cast object to map`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.CastObjectToMapDao>()
        val list = impl.query(mapOf("abc" to 0))
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `cast object to list`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.CastObjectToListDao>()
        val list = impl.query(listOf(0, 0))
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `reflect access bean property`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.ReflectAccessDao>()
        val a = test.java.integration.core.compile.success.template.template.ReflectAccessDao.A()
        a.a = "b"
        val list = impl.query(a)
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `boxed primitive type parameter`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )

        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.BoxedPrimitiveTypeParameterDao>()
        val list = impl.query(0)
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

    @Test
    fun `boxed primitive type property`() {
        initData(
            Person(1, "a"),
            Person(2, "b")
        )
        val impl = getJavaDaoInstance<test.java.integration.core.compile.success.template.template.BoxedPrimitiveTypePropertyDao>()
        val a = test.java.integration.core.compile.success.template.template.BoxedPrimitiveTypePropertyDao.A()
        a.id = -1.3
        val list = impl.query(a)
        assert(list.size == 1)
        assertContentEquals(list.map { it.id }, listOf(1))
    }

}