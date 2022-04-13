package com.github.afezeria.freedao.classic.runtime.context;

import com.github.afezeria.freedao.StatementType;
import com.github.afezeria.freedao.classic.runtime.*;
import com.github.afezeria.freedao.classic.runtime.dialect.DatabaseDialect;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author afezeria
 */
public class PageQueryContext extends DaoContext {

    private static final Logger logger = LoggerFactory.getLogger(PageQueryContext.class);
    protected static ThreadLocal<Page<?>> local = new ThreadLocal<>();

    private static final Map<SqlSignature<?, ?>, Boolean> disableParseSqlFlag = new ConcurrentHashMap<>();

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

            Select select = null;
            try {
                if (disableParseSqlFlag.get(signature) == null) {
                    select = (Select) CCJSqlParserUtil.parse(sql);
                }
            } catch (JSQLParserException e) {
                disableParseSqlFlag.put(signature, true);
                logger.debug("parse sql failure", e);
            } catch (ClassCastException e) {
                disableParseSqlFlag.put(signature, true);
                throw new RuntimeException("cannot convert non select sql into a paged sql");
            }

            if (!page.isSkipCount() && !page.isOrderByOnly()) {
                long count = execCountSql(connection, select, signature, sql, sqlArgs);

                page.setTotal(count);
                int offset = (page.getPageIndex() - 1) * page.getPageSize();
                logger.debug("count: {}, offset:{}", count, page.getOffset());
                if (count == 0L || offset >= count) {
                    return null;
                }
            }

            //language=SQL
            String pageSql = getPageSql(connection, select, page, sql);
            return getDelegate().execute(signature, methodArgs, pageSql, sqlArgs, executor, resultHandler);
        });
    }

    private static String getPageSql(Connection connection, Select select, Page<?> page, String originalSql) {
        String sql = originalSql;
        if (select != null && select.getSelectBody() instanceof PlainSelect) {
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            if (page.getOrderBy() != null) {
                selectBody.setOrderByElements(null);
            }
            if (!page.isOrderByOnly()) {
                selectBody.setLimit(null);
                selectBody.setOffset(null);
            }
            sql = select.toString();
        }
        try {
            sql = DatabaseDialect.getDialect(connection.getMetaData().getDatabaseProductName())
                    .getPageSql(page, originalSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    /**
     * 执行count查询
     *
     * @return
     */
    private static long execCountSql(Connection connection, Select select, SqlSignature<?, ?> signature, String originalSql, List<Object> sqlArgs) {
        String countSql;
        String sql = originalSql;
        if (select != null && select.getSelectBody() instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            if (plainSelect.isForUpdate() || plainSelect.getOrderByElements() != null || plainSelect.getLimit() != null || plainSelect.getOffset() != null) {
                boolean forUpdate = plainSelect.isForUpdate();
                Limit limit = plainSelect.getLimit();
                Offset offset = plainSelect.getOffset();
                List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
                plainSelect.setForUpdate(false);
                plainSelect.setLimit(null);
                plainSelect.setOffset(null);
                plainSelect.setOrderByElements(null);
                sql = plainSelect.toString();
                plainSelect.setForUpdate(forUpdate);
                plainSelect.setLimit(limit);
                plainSelect.setOffset(offset);
                plainSelect.setOrderByElements(orderByElements);

            }
        }
        countSql = "select count(*) from(\n" + sql + "\n) as _cot";
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
