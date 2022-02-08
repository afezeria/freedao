package com.github.afezeria.freedao.runtime.classic;

import com.github.afezeria.freedao.runtime.classic.context.TransactionContext;
import com.github.afezeria.freedao.runtime.classic.context.BaseDaoContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 */
public class DaoContext {
    protected DaoContext() {

    }

    protected DaoContext context = null;

    protected DaoContext delegate = null;

    public Connection getConnection() {
        return delegate.getConnection();
    }

    public <T> T withConnection(Function<Connection, T> supplier) {
        return delegate.withConnection(supplier);
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Builder() {
        }

        private final List<DaoContext> contexts = new ArrayList<>();

        public Builder withDefault(LinkedHashMap<String, DataSource> map) {
            contexts.add(new BaseDaoContext());
            contexts.add(new TransactionContext(map));
//            contexts.add(new LogContext());
            return this;
        }

        public Builder add(DaoContext context) {
            contexts.add(context);
            return this;
        }

        public DaoContext build() {
            if (contexts.isEmpty()) {
                throw new IllegalArgumentException("contexts is empty");
            }
            DaoContext context = null;
            DaoContext wrapper = new DaoContext();
            for (DaoContext daoContext : contexts) {
                if (context != null) {
                    daoContext.delegate = context;
                }
                daoContext.context = wrapper;
                context = daoContext;
            }
            wrapper.delegate = context;
            return wrapper;
        }
    }
}
