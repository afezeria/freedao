package test.java.failure.core.method.style.xml

import org.junit.Test
import test.BaseTest
import test.java.success.core.method.style.xml.EmptyTemplateDao

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