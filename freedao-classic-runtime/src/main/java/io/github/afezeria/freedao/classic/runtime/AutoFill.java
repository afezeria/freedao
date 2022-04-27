package io.github.afezeria.freedao.classic.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记值自动生成的字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    /**
     * 执行sql前填充数据
     */
    boolean before() default false;

    /**
     * 插入时填充
     */
    boolean insert() default true;

    /**
     * 更新时填充
     */
    boolean update() default false;

    /**
     * 用于获取字段值的类，默认值时表示字段值由数据库生成，
     * 自定义的生成器需要提供和ValueGenerator.gen方法签名一致的函数
     */
    Class<?> generator() default DbGenerator.class;
}