package com.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.METHOD)
public @interface Select {
    String[] items() default {};

    String table() default "";

    Class<?> tableClass() default Object.class;



    String where() default "";

}

