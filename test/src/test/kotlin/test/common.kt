package test

import com.google.testing.compile.Compilation
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.afezeria.freedao.classic.runtime.LogHelper
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import org.intellij.lang.annotations.Language
import org.slf4j.LoggerFactory
import java.io.File
import java.sql.Date
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp
import java.util.*
import javax.sql.DataSource

/**
 *
 */
class Common

val logger = LoggerFactory.getLogger(Common::class.java)

fun compile(vararg classNames: String): KotlinCompilation.Result {
    val processor = MainProcessor()
    val result = KotlinCompilation().apply {
        this.sources = classNames.map { getJavaSource(it) }.toList()
        annotationProcessors = listOf(processor)
        inheritClassPath = true
        messageOutputStream = System.out

    }.compile()
    return result
}

fun compile(vararg sources: SourceFile): KotlinCompilation.Result {
    val processor = MainProcessor()
    val result = KotlinCompilation().apply {
        this.sources = sources.toList()
        annotationProcessors = listOf(processor)
        inheritClassPath = true
        messageOutputStream = System.out // see diagnostics in real time
    }.compile()
    return result
}

fun getJavaSource(name: String): SourceFile {
    return SourceFile.fromPath(File("./src/main/java/test/$name.java"))
}

fun DataSource.execute(
    @Language("sql") sql: String,
    vararg params: Any?,
): MutableList<MutableMap<String, Any?>> {
    LogHelper.logSql(logger, sql)
    LogHelper.logArgs(logger, params.toList())
    return connection.use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            params.forEachIndexed { index, any ->
                stmt.setObject(index + 1, any)
            }
            stmt.execute()
            stmt.resultSet.toList()
        }
    }
}

fun ResultSet?.toList(): MutableList<MutableMap<String, Any?>> {
    if (this == null) return mutableListOf()
    val metaData = this.metaData
    val list = mutableListOf<MutableMap<String, Any?>>()
    while (next()) {
        val map = mutableMapOf<String, Any?>()
        (1..metaData.columnCount).forEach {
            val any = getObject(it)
            map[metaData.getColumnName(it)] = when (any) {
                null -> null
                is Time -> any.toLocalTime()
                is Date -> any.toLocalDate()
                is Timestamp -> any.toLocalDateTime()
                else -> any
            }
        }
        list.add(map)
    }
    return list
}

val Compilation.errorMessages: List<String>
    get() = errors().map { it.getMessage(Locale.getDefault()) }