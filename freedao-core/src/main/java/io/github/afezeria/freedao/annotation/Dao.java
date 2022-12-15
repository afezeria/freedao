package io.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dao {

    /**
     * 可选，指定的类必须由{@link io.github.afezeria.freedao.annotation.Table}注解，未指定时只能声明XML模板方法
     */
    Class<?> crudEntity() default Object.class;

    /**
     * ignore
     */
    String dialect() default "";
}
