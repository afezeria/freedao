package io.github.afezeria.freedao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author afezeria
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceValue {
    /**
     * {@link Join#id()}
     */
    String joinId();

    /**
     * 关联表的字段名
     */
    String columnName();
}
