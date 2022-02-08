package com.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.METHOD)
public @interface Delete {
    String table() default "";

    String where() default "";
}
