package com.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.TYPE)
public @interface Join {
    /**
     * 关联名称，类上多个Join注解中唯一
     */
    String name() default "";

    /**
     * 当前类中用于关联的字段的字段名
     */
    String[] associateKey() default {};

    /**
     * 关联表的键名
     */
    String[] tableKey() default {};

    /**
     * 关联表表名
     * tableName 和 tableClass只需要存在一个
     * {@method tab}
     */
    String tableName() default "";

    /**
     * 关联的表对应的实体类，该实体类上必须存在Table注解
     *
     * @return
     */
    Class<?> tableClass() default Object.class;
}
