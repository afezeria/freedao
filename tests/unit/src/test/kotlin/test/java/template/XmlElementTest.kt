package test.java.template

import org.junit.Test
import test.BaseTest
import test.errorMessages
import test.java.template.element.InvalidNodeBadDao
import test.java.template.element.MissingElementAttributeBadDao
import test.java.template.element.UnknownRootNodeBadDao

/**
 *
 * @author afezeria
 */
class XmlElementTest : BaseTest() {

    @Test
    fun `error, missing element attribute`() {
        compileFailure<MissingElementAttributeBadDao> {
            assert(
                errorMessages.contains("[line:2, column:6, element:if] missing required attribute:test")
            )
        }
    }

    @Test
    fun `error, unknown xml root node`() {
        compileFailure<UnknownRootNodeBadDao> {
            assert(
                errorMessages.contains("unknown statement type")
            )
        }
    }

    @Test
    fun `error, invalid node`() {
        compileFailure<InvalidNodeBadDao> {
            assert(
                errorMessages.contains("Invalid node:abc")
            )
        }
    }
}