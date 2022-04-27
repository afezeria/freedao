package io.github.afezeria.freedao.classic.runtime.context;

import io.github.afezeria.freedao.classic.runtime.ResultHandler;
import io.github.afezeria.freedao.classic.runtime.SqlExecutor;
import io.github.afezeria.freedao.classic.runtime.SqlSignature;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 */
public class TransactionContext extends DaoContext {

    DataSource dataSource;

    public TransactionContext(DataSource dataSource) {
        Objects.requireNonNull(dataSource);
        this.dataSource = dataSource;
    }

    private Connection currentConn = null;

    @Override
    public <T> T withConnection(Function<Connection, T> function) {
        try {
            if (currentConn != null) {
                return function.apply(currentConn);
            } else {
                try (Connection connection = dataSource.getConnection()) {
                    currentConn = connection;
                    return function.apply(connection);
                } finally {
                    currentConn = null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object[] buildSql(SqlSignature<?, ?> signature, Object[] args, Function<Object[], Object[]> buildSqlClosure) {
        throw new IllegalStateException("method not implemented");
    }

    @Override
    public <T, E> T execute(SqlSignature<T, E> signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T, E> executor, ResultHandler<E> resultHandler) {
        throw new IllegalStateException("method not implemented");
    }
}
