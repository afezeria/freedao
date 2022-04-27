package io.github.afezeria.freedao.classic.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Join {
    /**
     * 关联名称，类上多个Join注解中唯一
     */
    String id() default "";

    /**
     * 当前类中用于关联的字段的字段名
     */
    String[] foreignKey() default {};

    /**
     * 关联表的键名
     */
    String[] referencesKey() default {};

    /**
     * 关联表表名，tableName 和 tableClass只需要存在一个
     */
    String tableName() default "";

    /**
     * 关联的表对应的实体类，该实体类上必须存在Table注解，tableName 和 tableClass只需要存在一个
     */
    Class<?> tableClass() default Object.class;
}
