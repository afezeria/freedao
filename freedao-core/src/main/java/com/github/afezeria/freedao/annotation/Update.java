package com.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.METHOD)
public @interface Update {
    String table() default "";

    String[] columns() default "";

    String where() default "";
}
