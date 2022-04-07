package com.github.afezeria.freedao.classic.runtime;

import java.sql.Statement;

/**
 */
@FunctionalInterface
public interface ResultHandler<T> {
    T handle(Statement stmt, Object[] parameters);
}
