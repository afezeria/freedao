package io.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手动定义映射类型
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultMappings {
    /**
     * 自定义映射内容
     */
    Mapping[] value();

    /**
     * 是否按字段名生成映射
     * <p>
     * 为true时，只应用自定义映射，此时value不能为空数组
     * <p>
     * 为false时，会先按名称映射再处理value中的自定义映射，target相同时自定义映射会覆盖自动生成的映射
     */
    boolean onlyCustomMapping() default false;
}
