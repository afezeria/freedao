package test

import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import io.github.afezeria.freedao.processor.core.processingEnvironment
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject

/**
 *
 * @author afezeria
 */
interface UnitBaseTest {
    companion object {

        private val javaFileObject = JavaFileObjects.forSourceString(
            "Test",
            //language=java
            """
            import test.UnitBaseTest.TestProcessor.TestAnn;
            @TestAnn
            public class Test{}
        """.trimIndent()
        )
    }

    fun test(
        vararg javaFileObjects: JavaFileObject,
        fn: ProcessingEnvironment.() -> Unit
    ) {
        Compiler.javac()
            .withProcessors(TestProcessor(fn))
            .compile(
                javaFileObject,
                *javaFileObjects
            )
    }

    class TestProcessor(val testfn: ProcessingEnvironment.() -> Unit) : AbstractProcessor() {
        annotation class TestAnn

        override fun getSupportedSourceVersion(): SourceVersion {
            return SourceVersion.RELEASE_8
        }

        override fun getSupportedAnnotationTypes(): MutableSet<String> {
            return mutableSetOf(TestAnn::class.qualifiedName!!)
        }

        override fun process(
            annotations: MutableSet<out TypeElement>?,
            roundEnv: RoundEnvironment,
        ): Boolean {
            processingEnvironment = processingEnv
            testfn(processingEnv)
            return false
        }
    }
}