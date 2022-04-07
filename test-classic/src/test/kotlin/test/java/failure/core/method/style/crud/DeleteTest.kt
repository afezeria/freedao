package test.java.failure.core.method.style.crud

import org.junit.Test
import test.BaseTest
import test.Person
import test.java.failure.core.method.style.crud.delete.ParametersNotMatchDeleteBadDao

/**
 *
 */
class DeleteTest : BaseTest() {
    @Test
    fun `parameter types does not match primary key`() {
        compileFailure<ParametersNotMatchDeleteBadDao> {
            assertErrorMessageEquals("Missing parameter of type ${Person::class.qualifiedName}")
        }
    }
}