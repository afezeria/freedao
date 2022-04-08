package com.github.afezeria.freedao.classic.runtime.context;

import com.github.afezeria.freedao.classic.runtime.SqlExecutor;
import com.github.afezeria.freedao.classic.runtime.SqlSignature;

import java.util.List;
import java.util.function.Function;

/**
 *
 */
public class ExecutorContext extends DaoContext {
    public ExecutorContext(DaoContext delegate) {
        super(delegate);
    }

    @Override
    public Object[] buildSql(SqlSignature signature, Object[] args, Function<Object[], Object[]> sqlBuilder) {
        return sqlBuilder.apply(args);
    }

    @Override
    public <T> T execute(SqlSignature signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T> executor) {
        return delegate.withTx(connection -> executor.execute(connection, methodArgs, sql, sqlArgs));
    }
}
