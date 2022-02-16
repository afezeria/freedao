package test

import com.github.afezeria.freedao.annotation.Column
import com.github.afezeria.freedao.annotation.Table
import com.github.afezeria.freedao.processor.classic.contextVar
import com.github.afezeria.freedao.processor.core.MainProcessor
import com.github.afezeria.freedao.processor.core.debug
import com.github.afezeria.freedao.processor.core.toSnakeCase
import com.github.afezeria.freedao.runtime.classic.DaoContext
import com.google.testing.compile.Compilation
import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import com.zaxxer.hikari.HikariDataSource
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer
import java.nio.file.Path
import javax.sql.DataSource
import javax.tools.JavaFileObject
import javax.tools.JavaFileObject.Kind.CLASS
import kotlin.io.path.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties


/**
 *
 */
@RunWith(Parameterized::class)
abstract class BaseTest {
    init {
        debug = true
    }

    @Parameter(0)
    lateinit var env: DbEnv

    inline fun <reified T : Entity> initDataWithNullValue(vararg entities: T) = initData(T::class, true, *entities)
    inline fun <reified T : Entity> initData(vararg entities: T) = initData(T::class, false, *entities)

    @OptIn(ExperimentalStdlibApi::class)
    fun <T : Any> initData(kClass: KClass<T>, fillNull: Boolean, vararg entities: Entity) {
        env.dataSource.apply {
            val testResource =
                kClass.findAnnotations<DDL>().find { it.dialect == env.name }
                    ?: throw IllegalStateException()
            val table = kClass.findAnnotation<Table>()
                ?: throw IllegalStateException()
            execute("drop table if exists ${table!!.name}")
            execute(testResource.value)

            entities.forEach {
                val name2value = it::class.memberProperties.mapTo(mutableListOf()) { p ->
                    (p.findAnnotation<Column>()?.name?.takeIf { it.isNotEmpty() }
                        ?: p.name.toSnakeCase()) to p.getter.call(it)
                }.apply {
                    if (!fillNull) {
                        removeIf { it.second == null }
                    }
                }

                execute(
                    """
                    insert into ${table.name} (${name2value.joinToString { it.first }}) 
                    values (${name2value.joinToString { "?" }})
                 """,
                    *name2value.map { it.second }.toTypedArray()
                )
            }
        }
    }


    fun initTable(table: String, data: List<Map<String, Any?>> = emptyList()) {
        env.dataSource.execute("delete from $table")
        val fields = data[0].keys
        env.dataSource.execute(
            """
                    insert into $table (${fields.joinToString()}) 
                    values 
                    ${data.joinToString { fields.joinToString(prefix = "(", postfix = ")") { "?" } }}
                 """,
            *data.map { map ->
                fields.map { k -> map[k] }
            }.flatten().toTypedArray()
        )
    }

    class ByteClassLoader : ClassLoader() {
        fun loadClass(bytes: ByteArray): Class<*> {
            return defineClass(null, bytes, 0, bytes.size)
        }
    }

    inline fun <reified T : Any> getJavaDaoInstance(): T {
        return getImplInstance(T::class, Path("./src/main/java/" + T::class.qualifiedName?.replace('.', '/') + ".java"))
    }

    fun <T : Any> getImplInstance(clazz: KClass<T>, path: Path): T {
        val className = clazz.simpleName + "Impl"
        val javaFileObject = JavaFileObjects.forResource(path.normalize().toAbsolutePath().toUri().toURL())

        val compilation: Compilation = Compiler.javac()
            .withProcessors(MainProcessor())
            .withOptions("-parameters")
            .compile(javaFileObject)
        CompilationSubject.assertThat(compilation).succeeded()

        compilation.generatedSourceFiles().forEach {
            if (it.kind == JavaFileObject.Kind.SOURCE) {
                println("${it.name} ========================")
                println(it.openReader(true).readText())
            }
        }

        val readBytes =
            compilation.generatedFiles().find { it.name.contains(className) && it.kind == CLASS }!!
                .openInputStream().readBytes()
        val implClass = ByteClassLoader().loadClass(readBytes)
        val instance = implClass.constructors.first().newInstance()
        val declaredField = implClass.getDeclaredField(contextVar).apply {
            isAccessible = true
        }
        declaredField.set(instance, env.context)
        return instance as T
    }

    enum class EnvType {
        LOCAL, DOCKER, MEMORY
    }

    data class DbEnv(
        val type: EnvType,
        val name: String,
        val username: String? = null,
        val password: String? = null,
        val jdbcUrl: String,
        val driverClassName: String,
    ) {

        val dataSource: DataSource = HikariDataSource().apply {
            jdbcUrl = this@DbEnv.jdbcUrl
            username = this@DbEnv.username
            password = this@DbEnv.password
            driverClassName = this@DbEnv.driverClassName
        }


        val context = DaoContext.builder().withDefault(LinkedHashMap<String?, DataSource?>().apply {
            put("main", dataSource)
        }).build()

        fun find(table: String, where: String = "1 = 1"): MutableList<MutableMap<String, Any?>> {
            return dataSource.execute("select * from $table where $where")
        }

        override fun toString(): String {
            return "${type.name.lowercase()} $name"
        }
    }

    companion object {
        private val MYSQL_CONTAINER by lazy {
            MySQLContainer("mysql:8.0.28").apply {
                start()
            }
        }
        private val PG_CONTAINER by lazy {
            PostgreSQLContainer("postgres:14.1-alpine3.15").apply {
                withInitScript("sql/pg_init.sql")
                start()
            }
        }
        private val h2Env by lazy {
            arrayOf(
                DbEnv(
                    type = EnvType.MEMORY,
                    name = "pg",
                    jdbcUrl = "jdbc:h2:mem:;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/pg_init.sql';MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                    driverClassName = "org.h2.Driver",
                ),
//                DbEnv(
//                    type = EnvType.MEMORY,
//                    name = "mysql",
//                    jdbcUrl = "jdbc:h2:mem:;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/mysql_init.sql';MODE=MySQL;DATABASE_TO_LOWER=TRUE",
//                    driverClassName = "org.h2.Driver",
//                ),
            )
        }
        private val localEnv by lazy {
            arrayOf(DbEnv(
                type = EnvType.LOCAL,
                name = "pg",
                username = "test",
                password = "123456",
                jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                driverClassName = "org.postgresql.Driver",
            ))
        }

        private val containerEnv by lazy {
            arrayOf(
                DbEnv(
                    type = EnvType.DOCKER,
                    name = "pg",
                    username = PG_CONTAINER.username,
                    password = PG_CONTAINER.password,
                    jdbcUrl = PG_CONTAINER.jdbcUrl,
                    driverClassName = PG_CONTAINER.driverClassName,
                ),
                DbEnv(
                    type = EnvType.DOCKER,
                    name = "mysql",
                    username = MYSQL_CONTAINER.username,
                    password = MYSQL_CONTAINER.password,
                    jdbcUrl = MYSQL_CONTAINER.jdbcUrl,
                    driverClassName = MYSQL_CONTAINER.driverClassName,
                ),
            )
        }

        @JvmStatic
        @Parameters(name = "{index}: {0}")
        fun testParameters() = listOf(
//            *h2Env,
            *localEnv,
//            *containerEnv,
        )

    }
}
