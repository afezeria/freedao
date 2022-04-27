package io.github.afezeria.freedao.annotation;

import io.github.afezeria.freedao.ResultTypeHandler;

/**
 */
public @interface Mapping {
    /**
     * select list中列的名称
     */
    String source() default "";

    /**
     * 对象中的字段名
     */
    String target() default "";

    /**
     * 传入参数应为ResultTypeHandler的实现类
     */
    Class<?> typeHandler() default ResultTypeHandler.class;
}
