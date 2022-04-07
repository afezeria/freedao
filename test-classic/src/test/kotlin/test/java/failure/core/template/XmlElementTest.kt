package test.java.failure.core.template

import org.junit.Test
import test.BaseTest
import test.java.failure.core.template.element.InvalidNodeBadDao
import test.java.failure.core.template.element.MissingElementAttributeBadDao
import test.java.failure.core.template.element.UnknownRootNodeBadDao

/**
 *
 * @author afezeria
 */
class XmlElementTest : BaseTest() {

    @Test
    fun `error, missing element attribute`() {
        compileFailure<MissingElementAttributeBadDao> {
            assertErrorMessageEquals("[line:2, column:6, element:if] missing required attribute:test")
        }
    }

    @Test
    fun `error, unknown xml root node`() {
        compileFailure<UnknownRootNodeBadDao> {
            assertErrorMessageEquals("unknown statement type")
        }
    }

    @Test
    fun `error, invalid node`() {
        compileFailure<InvalidNodeBadDao> {
            assertErrorMessageEquals("Invalid node:abc")
        }
    }
}