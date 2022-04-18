package test.java.integration.core.compile.failure.template

import org.junit.Test
import test.BaseTest
import test.java.integration.core.compile.failure.template.element.InvalidNodeBadDao
import test.java.integration.core.compile.failure.template.element.MissingElementAttributeBadDao
import test.java.integration.core.compile.failure.template.element.UnknownRootNodeBadDao

/**
 *
 * @author afezeria
 */
class XmlElementTest : BaseTest() {

    @Test
    fun `error, missing element attribute`() {
        compileFailure<test.java.integration.core.compile.failure.template.element.MissingElementAttributeBadDao> {
            assertErrorMessageEquals("[line:2, column:6, element:if] missing required attribute:test")
        }
    }

    @Test
    fun `error, unknown xml root node`() {
        compileFailure<test.java.integration.core.compile.failure.template.element.UnknownRootNodeBadDao> {
            assertErrorMessageEquals("unknown statement type")
        }
    }

    @Test
    fun `error, invalid node`() {
        compileFailure<test.java.integration.core.compile.failure.template.element.InvalidNodeBadDao> {
            assertErrorMessageEquals("Invalid node:abc")
        }
    }
}