package io.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 表名
     */
    String name() default "";

    /**
     * 模式名
     */
    String schema() default "";

    /**
     * {@link Table#name()}的别名，优先级低于{@link Table#name()}
     */
    String value() default "";

    String database() default "";

    /**
     * 实体类的字段名数组
     */
    String[] primaryKeys() default {};
}
