package io.github.afezeria.freedao.processor.core.processor

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 *
 * @author afezeria
 */
class TypeDescriptionTest {
    @Test
    fun abc() {
        assertSoftly {

            listOf(
                "abc",
                "java.util.Map<java.lang.String,java.lang.String>",
                "a<b<c<f<d,g>>>,C<d>>",
            ).forEach {
                it shouldBe TypeDescription(it).toString()
            }
        }
    }

}