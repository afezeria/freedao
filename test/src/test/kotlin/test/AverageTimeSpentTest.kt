package test

import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import io.github.afezeria.freedao.processor.core.processor.apt.MainProcessor
import javax.annotation.processing.AbstractProcessor
import kotlin.io.path.Path

/**
 *
 * @author afezeria
 */

fun main() {
    val rootPath = Path("./test/src/test/java")
    val sources = rootPath.toFile().walk()
        .filter { it.isDirectory.not() }
        .filter { it.path.contains("failure").not() }
        .map { JavaFileObjects.forResource(it.toPath().normalize().toAbsolutePath().toUri().toURL()) }
        .toList()

    val lombokProcessor = Class.forName("lombok.launch.AnnotationProcessorHider\$AnnotationProcessor").getConstructor()
        .newInstance() as AbstractProcessor
    val compiler = Compiler.javac()
        .withProcessors(
            MainProcessor(),
            lombokProcessor,
        )
        .withOptions(
            "-parameters", *arrayOf(
                "-Afreedao.debug=true",
            )
        )
    val compilation = compiler.compile(sources)
    compilation.diagnostics().forEach { println(it.toString()) }
    CompilationSubject.assertThat(compilation).succeeded()


}
