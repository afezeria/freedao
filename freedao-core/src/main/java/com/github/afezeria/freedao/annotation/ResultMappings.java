package com.github.afezeria.freedao.annotation;

/**
 * 手动定义映射类型
 */
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
    boolean overrideAutoMapping() default true;
}
