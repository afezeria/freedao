package io.github.afezeria.freedao.classic.runtime;

/**
 * 标识引用字段
 */
public @interface ReferenceValue {
    String joinId() default "";

    String column() default "";

    String field() default "";
}
