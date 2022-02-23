package test.java.failure.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.failure.core.method.style.crud.all.ReturnStringAllBadDao

/**
 *
 * @author afezeria
 */
class AllTest : BaseTest() {

    @Test
    fun `invalid return type`() {
        compileFailure<ReturnStringAllBadDao> {
            assertErrorMessageEquals("The return type must be assignable to Collection<${Person::class.qualifiedName}>")
        }
    }
}