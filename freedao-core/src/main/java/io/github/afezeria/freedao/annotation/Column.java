package io.github.afezeria.freedao.annotation;

import io.github.afezeria.freedao.ParameterTypeHandler;
import io.github.afezeria.freedao.ResultTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解
 * <p>
 * 当使用标准crud方法时如果有多个实体字段映射到同一个数据库字段，
 * 且作为参数的实体的这些字段的值不为null且一致时会导致where条件为false
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
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
