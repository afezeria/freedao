package test.java.integration.classic.runtime

import io.github.afezeria.freedao.classic.runtime.SqlHelper
import io.github.afezeria.freedao.classic.runtime.context.*
import org.junit.Test
import test.BaseTest
import test.Person
import java.util.function.Supplier

/**
 *
 * @author afezeria
 */
class ContextParameterTest : BaseTest() {
    @Test
    fun dynamicTableName() {
        initData(DynamicTableNameEntity().setName("a"))
        env.withContext({
            DaoContext.create(
                TransactionContext(dataSource),
                ExecutorContext(),
                ParameterContext(mapOf("year" to 2000)),
                ProxyContext(),
            )
        }) {
            val impl = getJavaDaoInstance<DynamicTableNameDao>()
            val entity = impl.selectOneById(1)
            assert(entity.name == "a")
        }

        env.withContext({
            DaoContext.create(
                TransactionContext(dataSource),
                ExecutorContext(),
                ParameterContext(mapOf("year" to Supplier { 2000 })),
                ProxyContext(),
            )
        }) {
            val impl = getJavaDaoInstance<DynamicTableNameDao>()
            val entity = impl.selectOneById(1)
            assert(entity.name == "a")
        }
    }

    @Test
    fun tempContextParameter() {
        initData(Person(name = "a"), Person(name = "b"))
        val impl = getJavaDaoInstance<TempContextParameterDao>()
        val entity = SqlHelper.withContextParameter("id", 1L) {
            impl.select()
        }
        assert(entity.name == "a")
        val select = impl.select()
        assert(select == null)
    }

    @Test
    fun multipleTempContextParameter() {
        initData(Person(name = "a"), Person(name = "b"))
        val impl = getJavaDaoInstance<TempContextParameterDao>()
        val entity = SqlHelper.withContextParameters(mapOf("id" to 1L)) {
            impl.select()
        }
        assert(entity.name == "a")
        val select = impl.select()
        assert(select == null)
    }
}