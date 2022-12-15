package test.java.unit.core.extension

import com.google.testing.compile.JavaFileObjects
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.isResultTypeHandlerAndMatchType
import io.github.afezeria.freedao.processor.core.type
import org.intellij.lang.annotations.Language
import org.junit.Test
import test.UnitBaseTest
import javax.lang.model.type.TypeKind
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 *
 * @author afezeria
 */
class IsResultTypeHandlerAndMatchTypeTests : UnitBaseTest {
    companion object {
        private val classNameRegex = "public \\w+ (\\w+)".toRegex()
        private const val MISS_METHOD_MSG =
            "Invalid ResultTypeHandler:Handler, missing method:public static Object handleResult(Object,Class)"
    }

    private fun resultTypeHandlerTest(
        @Language("java") classText: String,
        targetType: KClass<*>,
        errorMsg: String
    ) {
        val className = classNameRegex.find(classText)!!.groupValues[1]
        test(
            JavaFileObjects.forSourceString(
                className, classText
            )
        ) {
            val type = elementUtils.getTypeElement(className).asType()
            val e = assertFailsWith<HandlerException> {
                type.isResultTypeHandlerAndMatchType(targetType.type)
            }
            assertEquals(e.message, errorMsg)
        }
    }

    @Test
    fun success() {
        val handler = JavaFileObjects.forSourceString(
            "Handler",
            """
                public class Handler {
                    public static String handleResult(Object abc, Class<?> clazz) {
                        throw new IllegalStateException();
                    }
                }
            """,
        )
        test(handler) {
            val type = elementUtils.getTypeElement("Handler").asType()
            assert(type.isResultTypeHandlerAndMatchType(String::class.type) != null)
            assert(type.isResultTypeHandlerAndMatchType(CharSequence::class.type) != null)
        }
    }

    @Test
    fun defaultClass() {
        test {
            val type = elementUtils.getTypeElement("io.github.afezeria.freedao.ResultTypeHandler").asType()
            assert(type.isResultTypeHandlerAndMatchType(String::class.type) == null)
        }
    }

    @Test
    fun emptyClass() {
        resultTypeHandlerTest(
            """
                public class Handler {
                    public static String a;
                }
            """,
            Any::class,
            MISS_METHOD_MSG
        )
    }

    @Test
    fun notClass() {
        resultTypeHandlerTest(
            "public interface Handler {}",
            Any::class,
            "ResultTypeHandler must be Class"
        )
        test {
            val type = typeUtils.getPrimitiveType(TypeKind.INT)

            val e = assertFailsWith<HandlerException> {
                type.isResultTypeHandlerAndMatchType(Any::class.type)
            }
            assertEquals(e.message, "ResultTypeHandler must be Class")
        }
    }

    @Test
    fun methodNameNotMatch() {
        resultTypeHandlerTest(
            """
                public class Handler{
                    public void abc() {
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
    }

    @Test
    fun numberOfMethodParameterNotMatch() {
        resultTypeHandlerTest(
            """
                public class Handler {
                    public Object handleResult(Object abc) {
                        throw new IllegalStateException();
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
    }

    @Test
    fun methodParameterTypeNotMatch() {
        resultTypeHandlerTest(
            """
                public class Handler {
                    public Object handleResult(String abc, Class<?> clazz) {
                        throw new IllegalStateException();
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
        resultTypeHandlerTest(
            """
                public class Handler {
                    public Object handleResult(Object abc, String clazz) {
                        throw new IllegalStateException();
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
    }

    @Test
    fun methodModifiersNotMatch() {
        resultTypeHandlerTest(
            """
                public class Handler {
                    public Object handleResult(Object abc, Class<?> clazz) {
                        throw new IllegalStateException();
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
        resultTypeHandlerTest(
            """
                public class Handler {
                    private static Object handleResult(Object abc, Class<?> clazz) {
                        throw new IllegalStateException();
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
    }

    @Test
    fun returnTypeNotMatch() {
        val text = """
            public class Handler {
                public static Number handleResult(Object abc, Class<?> clazz) {
                    throw new IllegalStateException();
                }
            }
        """

        resultTypeHandlerTest(
            text,
            Int::class,
            "Handler does not match java.lang.Integer type",
        )
        resultTypeHandlerTest(
            text,
            String::class,
            "Handler does not match java.lang.String type",
        )
    }
}