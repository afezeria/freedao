package test.java.template

import org.junit.Test
import test.BaseTest
import test.errorMessages
import test.java.template.expr.CompareBetweenTwoVariable1BadDao
import test.java.template.expr.CompareBetweenTwoVariable2BadDao
import test.java.template.expr.CompareDifferentTypeVariableBadDao
import test.java.template.expr.ParseFailureExprBadDao

/**
 *
 * @author afezeria
 */
class TestExprTest : BaseTest() {
    @Test
    fun `error, compare types that cannot be compared 1`() {
        compileFailure<CompareBetweenTwoVariable1BadDao> {
            assert(
                errorMessages.contains("[a > b] a is not comparable")
            )
        }
    }

    @Test
    fun `error, compare types that cannot be compared 2`() {
        compileFailure<CompareBetweenTwoVariable2BadDao> {
            assert(
                errorMessages.contains("[a > b] b is not comparable")
            )
        }
    }

    @Test
    fun `error, compare two different types of variables`() {
        compileFailure<CompareDifferentTypeVariableBadDao> {
            assert(
                errorMessages.contains("[a > b] Unable to compare a:java.lang.Integer and b:java.lang.String")
            )
        }
    }

    @Test
    fun `error, expr parse failure`() {
        compileFailure<ParseFailureExprBadDao> {
            assert(
                errorMessages.any {
                    it.matches("\\[a\\] 1:1 .*".toRegex())
                }
            )
        }
    }
}