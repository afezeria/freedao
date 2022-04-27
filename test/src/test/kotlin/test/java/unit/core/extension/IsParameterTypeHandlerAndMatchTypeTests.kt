package test.java.unit.core.extension

import com.google.testing.compile.JavaFileObjects
import io.github.afezeria.freedao.processor.core.HandlerException
import io.github.afezeria.freedao.processor.core.isParameterTypeHandlerAndMatchType
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
class IsParameterTypeHandlerAndMatchTypeTests : UnitBaseTest {
    companion object {
        private val classNameRegex = "public \\w+ (\\w+)".toRegex()
        private const val MISS_METHOD_MSG =
            "Invalid ParameterTypeHandler:Handler, missing method:public static Object handleParameter"
    }

    private fun parameterTypeHandlerTest(
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
                type.isParameterTypeHandlerAndMatchType(targetType.type)
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
                    public static Object handleParameter(CharSequence arg) {
                        return arg;
                    }
                }
            """,
        )
        test(handler) {
            val type = elementUtils.getTypeElement("Handler").asType()
            assert(type.isParameterTypeHandlerAndMatchType(String::class.type) != null)
            assert(type.isParameterTypeHandlerAndMatchType(CharSequence::class.type) != null)
        }
    }

    @Test
    fun alwaysMatchObject() {
        val handler = JavaFileObjects.forSourceString(
            "Handler",
            """
                public class Handler {
                    public static Object handleParameter(CharSequence arg) {
                        return arg;
                    }
                }
            """,
        )
        test(handler) {
            val type = elementUtils.getTypeElement("Handler").asType()
            assert(type.isParameterTypeHandlerAndMatchType(Any::class.type) != null)
        }

    }

    @Test
    fun defaultClass() {
        test {
            val type = elementUtils.getTypeElement("io.github.afezeria.freedao.ParameterTypeHandler").asType()
            assert(type.isParameterTypeHandlerAndMatchType(String::class.type) == null)
        }
    }

    @Test
    fun emptyClass() {
        parameterTypeHandlerTest(
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
    fun handlerNotClass() {
        parameterTypeHandlerTest(
            "public interface Handler {}",
            Any::class,
            "ParameterTypeHandler must be Class"
        )
        test {
            val type = typeUtils.getPrimitiveType(TypeKind.INT)

            val e = assertFailsWith<HandlerException> {
                type.isParameterTypeHandlerAndMatchType(Any::class.type)
            }
            assertEquals(e.message, "ParameterTypeHandler must be Class")
        }
    }

    @Test
    fun methodNameNotMatch() {
        parameterTypeHandlerTest(
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
        parameterTypeHandlerTest(
            """
                public class Handler {
                    public Object handleParameter() {
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
        parameterTypeHandlerTest(
            """
                public class Handler {
                    public static Object handleParameter(Integer abc) {
                        throw new IllegalStateException();
                    }
                }
            """,
            String::class,
            "Handler does not match java.lang.String type",
        )
    }

    @Test
    fun methodModifiersNotMatch() {
        parameterTypeHandlerTest(
            """
                public class Handler {
                    public Object handleParameter(Object abc) {
                        throw new IllegalStateException();
                    }
                }
            """,
            Any::class,
            MISS_METHOD_MSG,
        )
        parameterTypeHandlerTest(
            """
                public class Handler {
                    private static Object handleParameter(Object abc) {
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
                public static void handleParameter(Object abc) {
                    throw new IllegalStateException();
                }
            }
        """

        parameterTypeHandlerTest(
            text,
            Any::class,
            MISS_METHOD_MSG
        )
    }
}