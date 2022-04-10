package com.github.afezeria.freedao.classic.runtime;

import java.sql.ResultSet;

/**
 *
 */
@FunctionalInterface
public interface ResultHandler<T> {
    T handle(ResultSet rs, T item) throws Exception;
}
