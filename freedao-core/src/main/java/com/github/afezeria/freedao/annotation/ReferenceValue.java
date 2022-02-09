package com.github.afezeria.freedao.annotation;

/**
 * 标识引用字段
 *
 */
public @interface ReferenceValue {
    String joinName() default "";

    String column() default "";

    String field() default "";
}
