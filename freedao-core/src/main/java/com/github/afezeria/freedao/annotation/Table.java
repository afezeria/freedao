package com.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Table {
    String name() default "";

    String schema() default "";

    String database() default "";

    String[] primaryKeys() default {};
}
