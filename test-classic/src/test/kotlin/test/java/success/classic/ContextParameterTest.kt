package test.java.success.classic

import com.github.afezeria.freedao.classic.runtime.context.*
import org.junit.Test
import test.BaseTest
import test.Person
import test.java.success.classic.contextparameter.DynamicTableNameDao
import test.java.success.classic.contextparameter.DynamicTableNameEntity
import test.java.success.classic.contextparameter.TempContextParameterDao
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
        val entity = DaoHelper.withContextParameter("id", 1L) {
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
        val entity = DaoHelper.withContextParameters(mapOf("id" to 1L)) {
            impl.select()
        }
        assert(entity.name == "a")
        val select = impl.select()
        assert(select == null)
    }
}