package com.github.afezeria.freedao.runtime.classic;

import java.sql.Statement;

/**
 */
@FunctionalInterface
public interface ResultHandler<T> {
    T handle(Statement stmt, Object[] parameters);
}
