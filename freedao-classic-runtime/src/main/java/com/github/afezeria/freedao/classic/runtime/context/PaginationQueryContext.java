package com.github.afezeria.freedao.classic.runtime.context;

import com.github.afezeria.freedao.StatementType;
import com.github.afezeria.freedao.classic.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author afezeria
 */
public class PaginationQueryContext extends DaoContext {

    private static final Logger logger = LoggerFactory.getLogger(PaginationQueryContext.class);
    protected static ThreadLocal<Page<?>> local = new ThreadLocal<>();

    @Override
    public <T, E> T execute(SqlSignature<T, E> signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T, E> executor, ResultHandler<E> resultHandler) {
        return getDelegate().withConnection(connection -> {
            Page<?> page = local.get();
            if (page == null) {
                return getDelegate().execute(signature, methodArgs, sql, sqlArgs, executor, resultHandler);
            }
            if (page.getRecords() != null) {
                throw new IllegalStateException("the closure of DaoHelper.pagination can only contain one query");
            }
            if (signature.getType() != StatementType.SELECT) {
                throw new UnsupportedOperationException(signature.getType() + " statement does not support pagination");
            }
            if (!Collection.class.isAssignableFrom(signature.getReturnType())) {
                throw new UnsupportedOperationException("single row query does not support pagination");
            }

            long count = execCountSql(connection, signature, sql, sqlArgs);

            page.setCount(count);
            int offset = (page.getPageIndex() - 1) * page.getPageSize();
            logger.debug("count: {}, offset:{}", count, offset);
            if (count == 0L || offset >= count) {
                try {
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            //language=SQL
            String pagingSql = "select * from (" + sql + ") as _pagination limit " + page.getPageSize() + " offset " + offset;
            return getDelegate().execute(signature, methodArgs, pagingSql, sqlArgs, executor, resultHandler);
        });
    }

    private static long execCountSql(Connection connection, SqlSignature signature, String originalSql, List<Object> sqlArgs) {

        String countSql = null;
        countSql = "select count(*) from (" + originalSql + ") as _cot";
        if (logger.isDebugEnabled()) {
            LogHelper.logSql(logger, countSql);
            LogHelper.logArgs(logger, sqlArgs);
        }

        try (PreparedStatement stmt = connection.prepareStatement(countSql)) {
            for (int i = 0; i < sqlArgs.size(); i++) {
                stmt.setObject(i + 1, sqlArgs.get(i));
            }
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
