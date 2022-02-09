package com.github.afezeria.freedao.runtime.classic;

import java.sql.Connection;
import java.util.List;

/**
 */
@FunctionalInterface
public interface SqlExecutor<T> {
    T executor(Connection connection,
               Object[] methodArgs,
               String sql, List<Object> args);
}
