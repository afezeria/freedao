package com.github.afezeria.freedao.runtime.classic.context;

import com.github.afezeria.freedao.runtime.classic.DaoContext;
import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
import com.github.afezeria.freedao.runtime.classic.SqlSignature;

import java.util.List;
import java.util.function.Function;

/**
 */
public class BaseDaoContext extends DaoContext {
    @Override
    public Object[] buildSql(SqlSignature signature, Object[] args, Function<Object[], Object[]> sqlBuilder) {
        return sqlBuilder.apply(args);
    }

    @Override
    public <T> T execute(SqlSignature signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T> executor) {
        return context.withConnection(connection -> {
            return executor.executor(connection, methodArgs, sql, sqlArgs);
        });
    }
}
