package test

import org.intellij.lang.annotations.Language

/**
 *
 * @author afezeria
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Repeatable
annotation class DDL(
    val dialect: String,
    @Language("SQL")
    val value: String,
)