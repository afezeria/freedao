package test.java.failure.core.template

import org.junit.Test
import test.BaseTest
import test.java.failure.core.template.element.ErrorParameterTypeHandlerNameBadDao

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