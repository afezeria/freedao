package io.github.afezeria.freedao.classic.runtime;

/**
 * 事务传播行为
 *
 */
enum Propagation {
    /**
     * 如果存在一个事务则使用当前事务，则开启一个新的事务
     */
    REQUIRED,

    /**
     * 如果当前存在事务则使用当前事务，否则非事务执行
     */
    SUPPORTS,

    /**
     * 如果当前存在事务则使用当前事务，否则抛出异常
     */
    MANDATORY,

    /**
     * 总是开启新事务
     */
    REQUIRES_NEW,

    /**
     * 非事务执行，如果当前存在事务则挂起该事务
     */
    NOT_SUPPORTED,

    /**
     * 如果当前存在事务则抛出异常
     */
    NEVER,

    /**
     * 如果当前存在事务则创建嵌套事务执行，否则行为和[REQUIRED]一致
     */
    NESTED;
}