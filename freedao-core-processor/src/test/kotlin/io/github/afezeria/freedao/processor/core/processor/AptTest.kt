package io.github.afezeria.freedao.processor.core.processor

import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import org.intellij.lang.annotations.Language
import kotlin.test.Test

/**
 *
 * @author afezeria
 */
class AptTest {
    @Test
    fun abc() {
        val javaSource= JavaFileObjects.forSourceLines(
            "example.MyMap",
            """
import java.util.HashMap;
import io.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao
public class MyMap<T> extends HashMap<String,T> {
}
            """.trimIndent()
        )
        val compilation = Compiler.javac()
            .withProcessors(
                MainProcessor(),
            )
            .compile(javaSource)
        compilation.diagnostics().forEach { println(it.toString()) }

    }
}