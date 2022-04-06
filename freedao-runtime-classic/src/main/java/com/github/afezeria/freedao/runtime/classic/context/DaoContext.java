package com.github.afezeria.freedao.runtime.classic.context;

import com.github.afezeria.freedao.runtime.classic.SqlExecutor;
import com.github.afezeria.freedao.runtime.classic.SqlSignature;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.function.Function;

/**
 *
 */
public abstract class DaoContext {
    protected DaoContext(DaoContext delegate) {
        this.delegate = delegate;
    }

    protected DaoContext delegate;

    public <T> T withTx(Function<Connection, T> supplier) {
        return delegate.withTx(supplier);
    }


    public Object[] buildSql(
            SqlSignature signature,
            Object[] args,
            Function<Object[], Object[]> sqlBuilder
    ) {
        return delegate.buildSql(signature, args, sqlBuilder);
    }

    public <T> T execute(
            SqlSignature signature,
            Object[] methodArgs,
            String sql,
            List<Object> sqlArgs,
            SqlExecutor<T> executor
    ) {
        return delegate.execute(signature, methodArgs, sql, sqlArgs, executor);
    }

    public static DaoContext create(DataSource dataSource) {
        return new ParameterContext(new ExecutorContext(new TransactionContext(dataSource)), null);
    }

}
