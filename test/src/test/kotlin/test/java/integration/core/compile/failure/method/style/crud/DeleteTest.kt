package test.java.integration.core.compile.failure.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person

/**
 *
 */
class DeleteTest : BaseTest() {
    @Test
    fun `parameter types does not match primary key`() {
        compileFailure<test.java.integration.core.compile.failure.method.style.crud.delete.ParametersNotMatchDeleteBadDao> {
            assertErrorMessageEquals("Missing parameter of type ${Person::class.qualifiedName}")
        }
    }
}