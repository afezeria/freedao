package test.java.success.core.template

import com.github.afezeria.freedao.processor.core.template.element.TextElement.Companion.namedSqlParameterRegex
import com.github.afezeria.freedao.processor.core.template.element.TextElement.Companion.namedStringParameterRegex
import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.core.template.element.CastObjectToHandlerParameterTypeDao
import test.java.success.core.template.element.PersonNameEnum
import test.java.success.core.template.element.SqlArgumentWithTypeHandler1Dao
import test.java.success.core.template.element.SqlArgumentWithTypeHandler2Dao
import kotlin.test.assertContentEquals

/**
 *
 * @author afezeria
 */
class TextElementTest : BaseTest() {
    @Test
    fun stringSubstitutionRegexTest() {
        val str = "a\${a}b\${c.d.9}\${ #{}"
        assertContentEquals(str.split(namedStringParameterRegex), listOf("a", "b", "\${ #{}"))
        assertContentEquals(
            namedStringParameterRegex.findAll(str).mapTo(mutableListOf()) { it.groupValues[1] }, listOf("a", "c.d.9")
        )
    }

    @Test
    fun sqlArgumentRegexTest() {
        listOf(
            Triple("#{abc}", "abc", ""),
            Triple("#{abc,typeHandler=java.c}", "abc", "java.c"),
            Triple("#{a.2._3,typeHandler=java.c}", "a.2._3", "java.c"),
            Triple("#{abc,typeHandler=java.aou oaeur422c}", "abc", "java.aou oaeur422c"),
        ).forEach { (s, v, t) ->
            val replace = s.replace(namedSqlParameterRegex) {
                assert(it.groupValues[1] == v)
                assert(it.groupValues[2] == t)
                "?"
            }
            assert(replace == "?")

        }
    }

    @Test
    fun parameterTypeHandlerTest() {
        initData(Person(1, "a"), Person(2, "a"), Person(3, "b"))
        val impl = getJavaDaoInstance<SqlArgumentWithTypeHandler1Dao>()
        val list = impl.select(PersonNameEnum.a)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `expression type and the parameter type of handle method are both object`() {
        initData(Person(1, "a"), Person(2, "a"), Person(3, "b"))
        val impl = getJavaDaoInstance<SqlArgumentWithTypeHandler2Dao>()
        val list = impl.select(PersonNameEnum.a)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }

    @Test
    fun `cast expression type to the type of handle method`() {
        initData(Person(1, "a"), Person(2, "a"), Person(3, "b"))
        val impl = getJavaDaoInstance<CastObjectToHandlerParameterTypeDao>()
        val list = impl.select(PersonNameEnum.a)
        assert(list.size == 2)
        assertContentEquals(list.map { it.id }, listOf(1, 2))
    }

}