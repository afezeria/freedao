package io.github.afezeria.freedao.classic.runtime;

/**
 * 事务隔离级别
 */
enum TransactionIsolation {
    NONE,
    READ_COMMITTED,
    READ_UNCOMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE;
}
