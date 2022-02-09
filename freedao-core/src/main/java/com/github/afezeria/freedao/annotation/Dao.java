package com.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.TYPE)
public @interface Dao {
    Class<?> crudEntity() default Object.class;

    String dialect() default "";
}
