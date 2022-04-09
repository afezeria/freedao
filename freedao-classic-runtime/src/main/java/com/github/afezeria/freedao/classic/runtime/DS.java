package com.github.afezeria.freedao.classic.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定数据源标识以及匹配方式
 *
 * @author afezeria
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DS {
    /**
     * 数据源标识
     */
    String value();

    /**
     * 为true时，根据value的值进行对数据源进行前缀匹配
     * 为false时，根据value的值对数据源进行精确匹配
     */
    boolean prefix() default true;
}
