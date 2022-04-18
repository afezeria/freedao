package test.java.integration.core.compile.failure.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 * @author afezeria
 */
class AllTest : BaseTest() {

    @Test
    fun `invalid return type`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.list.ReturnStringAllBadDao> {
            assertErrorMessageEquals("The return type must be assignable to Collection<${Person::class.qualifiedName}>")
        }
    }
}