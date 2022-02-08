package com.github.afezeria.freedao.annotation;

import com.github.afezeria.freedao.ParameterTypeHandler;
import com.github.afezeria.freedao.ResultTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.FIELD)
public @interface Column {
    String name() default "";

    /**
     * 是否为数据库表字段
     */
    boolean exist() default true;

    /**
     * 标准方法插入时是否使用该字段
     */
    boolean insert() default true;

    /**
     * 标准方法更新时是否使用该字段
     */
    boolean update() default true;

    /**
     * 传入参数应为ResultTypeHandler的实现类
     */
    Class<?> parameterTypeHandle() default ParameterTypeHandler.class;

    /**
     * 传入参数应为ResultTypeHandler的实现类
     */
    Class<?> resultTypeHandle() default ResultTypeHandler.class;

}
