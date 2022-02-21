package test.java.method.style.xml

import org.junit.Test
import test.BaseTest
import test.errorMessages

/**
 *
 * @author afezeria
 */
class XmlTemplateTest : BaseTest() {
    @Test
    fun `error, empty template string`() {
        compileFailure<EmptyTemplateDao> {
            assert(
                errorMessages.contains("Xml template cannot be blank")
            )
        }
    }
}