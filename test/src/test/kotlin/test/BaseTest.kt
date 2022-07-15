package test

import com.google.testing.compile.Compilation
import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.zaxxer.hikari.HikariDataSource
import io.github.afezeria.freedao.annotation.Column
import io.github.afezeria.freedao.classic.processor.contextVar
import io.github.afezeria.freedao.classic.runtime.context.DaoContext
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import io.github.afezeria.freedao.processor.core.toSnakeCase
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer
import java.io.File
import java.nio.file.Path
import javax.sql.DataSource
import javax.tools.JavaFileObject
import javax.tools.JavaFileObject.Kind.CLASS
import kotlin.io.path.Path
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotations


/**
 *
 */
@RunWith(Parameterized::class)
abstract class BaseTest {

    @Parameter(0)
    lateinit var env: DbEnv

    inline fun <reified T : Entity> initDataWithNullValue(vararg entities: T) = initData(T::class, true, *entities)
    inline fun <reified T : Entity> initData(vararg entities: T) = initData(T::class, false, *entities)

    @OptIn(ExperimentalStdlibApi::class)
    fun <T : Entity> initData(kClass: KClass<T>, fillNull: Boolean = false, vararg entities: T) {
        env.dataSource.apply {
            val testResource =
                kClass.findAnnotations<DDL>().find { it.dialect == env.name }
                    ?: throw IllegalStateException()
            val tableName = getTableNameFromCreateTableStatement(testResource.value)
            execute("drop table if exists $tableName")
            execute(testResource.value)

            entities.forEach {
                val name2value = it::class.java.declaredFields
                    .filter { f ->
                        f.getAnnotation(Column::class.java)
                            ?.let { it.exist && (it.insert || it.update) }
                            ?: true
                    }.mapTo(mutableListOf()) { f ->
                        f.trySetAccessible()
                        (f.getAnnotation(Column::class.java)?.name?.takeIf { it.isNotEmpty() }
                            ?: f.name.toSnakeCase()) to f.get(it)
                    }.apply {
                        if (!fillNull) {
                            removeIf { it.second == null }
                        }
                    }

                execute(
                    """
                    insert into $tableName (${name2value.joinToString { it.first }}) 
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

    inline fun <reified T : Any> getJavaDaoInstance(vararg types: KClass<*>): T {
        return getJavaDaoInstance(T::class, types.toList())
    }

    fun <T : Any> getJavaDaoInstance(clazz: KClass<T>, types: List<KClass<*>>): T {
        fun class2Path(clazz: KClass<*>) = Path("./src/test/java/" + clazz.qualifiedName?.replace('.', '/') + ".java")

        val compilation = compileJava(
            class2Path(clazz),
            *types.map { class2Path(it) }.toTypedArray()
        )
        CompilationSubject.assertThat(compilation).succeeded()

        compilation.generatedSourceFiles().forEach {
            if (it.kind == JavaFileObject.Kind.SOURCE) {

                println("${it.name} ====================")
                println(it.openReader(true).readText())
            }
        }

        val className = clazz.simpleName + "Impl"
        val readBytes = compilation.generatedFiles()
            .find {
                it.name.contains(className) && it.kind == CLASS
            }!!.openInputStream()
            .readBytes()
        @Suppress("UNCHECKED_CAST")
        return loadClass(readBytes) as T
    }

    inline fun <reified T : Any> getKotlinDaoInstance(): T {
        return getKotlinDaoInstance(T::class)
    }

    fun <T : Any> getKotlinDaoInstance(clazz: KClass<T>): T {

        val compilation = KotlinCompilation().apply {
            this.sources = listOf(
                SourceFile.fromPath(File("./src/test/kotlin/" + clazz.qualifiedName?.replace('.', '/') + ".kt")),
            )
            annotationProcessors = listOf(MainProcessor())
            inheritClassPath = true
        }
        val result = compilation.compile()
        assert(result.exitCode == KotlinCompilation.ExitCode.OK)
        result.generatedFiles.forEach {
            if (it.extension == "java" && it.nameWithoutExtension == clazz.simpleName + "Impl") {
                println(it.name + "====================")
                println(it.readText())
            }
        }

        val bytes = result.generatedFiles.find {
            it.extension == "class" && it.nameWithoutExtension == clazz.simpleName + "Impl"
        }!!.readBytes()
        @Suppress("UNCHECKED_CAST")
        return loadClass(bytes) as T
    }

    var tempClassLoader: ByteClassLoader? = null

    fun loadClass(bytes: ByteArray): Any {
        tempClassLoader = ByteClassLoader()
        val implClass = tempClassLoader!!.loadClass(bytes)
        val instance = implClass.constructors.first().newInstance()
        val declaredField = implClass.getDeclaredField(contextVar).apply {
            isAccessible = true
        }
        declaredField.set(instance, env.contextWrapper)
        return instance
    }

    inline fun <reified T : Any> compileFailure(block: Compilation.() -> Unit) {
        return compileJava(
            Path("./src/test/java/" + T::class.qualifiedName?.replace('.', '/') + ".java")
        ).let {
            CompilationSubject.assertThat(it).failed()

            block.invoke(it)
        }
    }

    fun Compilation.assertErrorMessageEquals(msg: String) {
        assert(errorMessages.size == 1)
        assert(errorMessages[0] == msg)
    }


    fun compileJava(vararg path: Path): Compilation {
        val javaFileObjects = path.map { JavaFileObjects.forResource(it.normalize().toAbsolutePath().toUri().toURL()) }

//        val lombokProcessor = lombokProcessorClass.getConstructor().newInstance() as AbstractProcessor
        val compilation: Compilation = Compiler.javac()
            .withProcessors(
                MainProcessor(),
//                lombokProcessor
            )
            .withOptions("-parameters", *env.aptArgs)
            .compile(javaFileObjects)
        compilation.diagnostics().forEach { println(it.toString()) }
        return compilation
    }

    fun <T : Any> getImplInstance(clazz: KClass<T>, path: Path): T {
        val compilation = compileJava(path)
        CompilationSubject.assertThat(compilation).succeeded()

        compilation.generatedSourceFiles().forEach {
            if (it.kind == JavaFileObject.Kind.SOURCE) {

                println("${it.name} ========================")
                println(it.openReader(true).readText())
            }
        }

        val className = clazz.simpleName + "Impl"
        val readBytes =
            compilation.generatedFiles().find { it.name.contains(className) && it.kind == CLASS }!!
                .openInputStream().readBytes()
        val implClass = ByteClassLoader().loadClass(readBytes)
        val instance = implClass.constructors.first().newInstance()
        val declaredField = implClass.getDeclaredField(contextVar).apply {
            isAccessible = true
        }
        declaredField.set(instance, env.contextWrapper)
        @Suppress("UNCHECKED_CAST")
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

        val aptArgs: Array<String>
            get() {
                return arrayOf(
                    "-Afreedao.debug=true",
                    when (name) {
                        "pg" -> "-Afreedao.quote=\""
                        "mysql" -> "-Afreedao.quote=`"
                        else -> ""
                    }
                )
            }

        val dataSource: DataSource = HikariDataSource().apply {
            jdbcUrl = this@DbEnv.jdbcUrl
            username = this@DbEnv.username
            password = this@DbEnv.password
            driverClassName = this@DbEnv.driverClassName
        }

        val defaultContext = DaoContext.create(dataSource)

        val contextWrapper = object : DaoContext() {}.apply { delegate = defaultContext }

        fun <T> withContext(contextProvide: DbEnv.() -> DaoContext, closure: () -> T): T {
            val field = DaoContext::class.java.declaredFields.find { it.name == "delegate" }!!
            field.trySetAccessible()
            field.set(contextWrapper, contextProvide(this))
            val value = closure()
            field.set(contextWrapper, defaultContext)
            return value
        }

        fun find(table: String, where: String = "1 = 1"): MutableList<MutableMap<String, Any?>> {
            return dataSource.execute("select * from $table where $where")
        }

        override fun toString(): String {
            return "${type.name.lowercase()} $name"
        }
    }

    companion object {
        private val regex = "create table\\W+(\\w+)".toRegex()
        private fun getTableNameFromCreateTableStatement(string: String): String {
            return regex.find(string)!!.groupValues[1]
        }

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
        private val pgH2Env by lazy {
            arrayOf(
                DbEnv(
                    type = EnvType.MEMORY,
                    name = "pg",
                    jdbcUrl = "jdbc:h2:mem:;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/pg_init.sql';MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                    driverClassName = "org.h2.Driver",
                ),
            )
        }
        private val h2Env by lazy {
            arrayOf(
                DbEnv(
                    type = EnvType.MEMORY,
                    name = "pg",
                    jdbcUrl = "jdbc:h2:mem:;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/pg_init.sql';MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
                    driverClassName = "org.h2.Driver",
                ),
                DbEnv(
                    type = EnvType.MEMORY,
                    name = "mysql",
                    jdbcUrl = "jdbc:h2:mem:;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:sql/mysql_init.sql';MODE=MySQL;DATABASE_TO_LOWER=TRUE",
                    driverClassName = "org.h2.Driver",
                ),
            )
        }
        private val localEnv by lazy {
            arrayOf(
                DbEnv(
                    type = EnvType.LOCAL,
                    name = "pg",
                    username = "test",
                    password = "123456",
                    jdbcUrl = "jdbc:postgresql://localhost:5432/test",
                    driverClassName = "org.postgresql.Driver",
                )
            )
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
//            *pgH2Env,
//            *h2Env,
            *localEnv,
//            *containerEnv,
        )

    }
}
