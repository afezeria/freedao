package test

import org.intellij.lang.annotations.Language
import java.lang.annotation.Inherited

/**
 *
 * @author afezeria
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable
@Inherited
annotation class DDL(
    val dialect: String,
    @Language("sql")
    val value: String,
)