package com.github.afezeria.freedao.classic.runtime.context;

import com.github.afezeria.freedao.classic.runtime.ResultHandler;
import com.github.afezeria.freedao.classic.runtime.SqlExecutor;
import com.github.afezeria.freedao.classic.runtime.SqlSignature;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 */
public abstract class DaoContext {
    private DaoContext delegate;

    public synchronized void setDelegate(DaoContext delegate) {
        Objects.requireNonNull(delegate);
        if (this.delegate == null) {
            this.delegate = delegate;
        }
    }

    public DaoContext getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("uninitialized context");
        }
        return delegate;
    }

    public <T> T withConnection(Function<Connection, T> supplier) {
        return getDelegate().withConnection(supplier);
    }


    public Object[] buildSql(SqlSignature<?, ?> signature, Object[] args, Function<Object[], Object[]> buildSqlClosure) {
        return getDelegate().buildSql(signature, args, buildSqlClosure);
    }

    public <T, E> T execute(SqlSignature<T, E> signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T, E> executor, ResultHandler<E> resultHandler) {
        return getDelegate().execute(signature, methodArgs, sql, sqlArgs, executor, resultHandler);
    }

    public <T, E> T proxy(SqlSignature<T, E> signature, Object... methodArgs) {
        return delegate.proxy(signature, methodArgs);
    }

    public static DaoContext create(DataSource dataSource) {
        return create(
                new TransactionContext(dataSource),
                new ExecutorContext(),
                new PaginationQueryContext(),
                new ParameterContext(null),
                new ProxyContext()
        );
    }

    public static DaoContext create(DaoContext... contexts) {
        if (contexts.length == 0) {
            throw new IllegalArgumentException("contexts.length must be greater than 0");
        }
        if (contexts.length == 1) {
            return contexts[0];
        }
        for (int i = 1; i < contexts.length; i++) {
            contexts[i].setDelegate(contexts[i - 1]);
        }
        return contexts[contexts.length - 1];
    }

}
