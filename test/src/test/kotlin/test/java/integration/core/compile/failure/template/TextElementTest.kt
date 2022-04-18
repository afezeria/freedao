package test.java.integration.core.compile.failure.template

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class TextElementTest : BaseTest() {
    @Test
    fun `error class name`() {
        compileFailure<test.java.integration.core.compile.failure.template.element.ErrorParameterTypeHandlerNameBadDao> {
            assertErrorMessageEquals("class not found:a b c")
        }
    }

}