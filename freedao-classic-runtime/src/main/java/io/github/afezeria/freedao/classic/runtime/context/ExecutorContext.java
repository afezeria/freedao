package io.github.afezeria.freedao.classic.runtime.context;

import io.github.afezeria.freedao.classic.runtime.LogHelper;
import io.github.afezeria.freedao.classic.runtime.ResultHandler;
import io.github.afezeria.freedao.classic.runtime.SqlExecutor;
import io.github.afezeria.freedao.classic.runtime.SqlSignature;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;

/**
 *
 */
public class ExecutorContext extends DaoContext {
    @Override
    public Object[] buildSql(SqlSignature<?, ?> signature, Object[] args, Function<Object[], Object[]> buildSqlClosure) {
        return buildSqlClosure.apply(args);
    }

    @Override
    public <T, E> T execute(SqlSignature<T, E> signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T, E> executor, ResultHandler<E> resultHandler) {
        return getDelegate().withConnection(connection -> {
            try {
                Logger logger = signature.getLogger();
                if (logger.isDebugEnabled()) {
                    LogHelper.logSql(logger, sql);
                    LogHelper.logArgs(logger, sqlArgs);
                }
                return executor.execute(connection, methodArgs, sql, sqlArgs, resultHandler);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
