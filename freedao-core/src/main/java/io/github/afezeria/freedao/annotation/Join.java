package io.github.afezeria.freedao.annotation;

import java.lang.annotation.*;

/**
 * @author afezeria
 */
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Join.List.class)
public @interface Join {
    /**
     * 唯一标识，同一实体类上多个Join之间id不能相同
     * <p>
     * 不能为_main，且必须符合模式：_[a-zA-Z0-9_]+
     * <p>
     * 因为值会被用于关联属性的结果集列名的前缀，建议尽量简短
     */
    String id();

    /**
     * 关联表名
     * <p>entityClass不为Object.class时该属性可以忽略</p>
     */
    String table() default "";

    /**
     * 关联表的模式名，需要和当前实体的模式名一致
     * <p>entityClass不为Object.class时该属性可以忽略</p>
     */
    String schema() default "";

    /**
     * 关联表中用于关联的字段
     * <p>
     * 当entityClass不为Object.class时该属性可以忽略
     * <p>
     * 当entityClass不为Object.class时存在以下规则：
     * <p>
     * - 该属性为空数组时将使用entityClass的主键作为关联字段
     * <p>
     * - 编译时会检查字段是否存在，如果要使用的字段在entityClass中不存请设置{@link Join#table()}而不是{@link Join#entityClass()}
     * <p>
     * - entityClass中关联字段的类型必须和当前表中foreignKey指定的字段数量和类型一致
     */
    String[] referenceKey() default {};

    /**
     * 当前表中用于关联的字段的列名
     */
    String[] foreignKey();

    /**
     * 关联的实体类型
     */
    Class<?> entityClass() default Object.class;

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Join[] value();
    }
}
