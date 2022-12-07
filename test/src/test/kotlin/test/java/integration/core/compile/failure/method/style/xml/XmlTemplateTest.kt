package test.java.integration.core.compile.failure.method.style.xml

import org.junit.Test
import test.BaseTest

/**
 *
 * @author afezeria
 */
class XmlTemplateTest : BaseTest() {
    @Test
    fun `empty template string`() {
        compileFailure<EmptyTemplateDao> {
            assertErrorMessageEquals("Xml template cannot be blank")
        }
    }
}