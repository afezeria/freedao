package test.java.integration.core.compile.failure.template

import org.junit.Test
import test.BaseTest
import test.java.integration.core.compile.failure.template.element.ErrorParameterTypeHandlerNameBadDao

/**
 *
 * @author afezeria
 */
class TextElementTest : BaseTest() {
    @Test
    fun `error class name`() {
        compileFailure<ErrorParameterTypeHandlerNameBadDao> {
            assertErrorMessageEquals("class not found:a b c")
        }
    }

}