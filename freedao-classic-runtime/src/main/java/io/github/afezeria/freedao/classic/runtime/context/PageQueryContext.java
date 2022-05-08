package io.github.afezeria.freedao.classic.runtime.context;

import io.github.afezeria.freedao.StatementType;
import io.github.afezeria.freedao.classic.runtime.*;
import io.github.afezeria.freedao.classic.runtime.dialect.DatabaseDialect;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author afezeria
 */
public class PageQueryContext extends DaoContext {

    private static final Logger logger = LoggerFactory.getLogger(PageQueryContext.class);
    public static ThreadLocal<Page<?>> local = new ThreadLocal<>();

    private static final Map<SqlSignature<?, ?>, Boolean> disableParseSqlFlag = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T, E> T execute(SqlSignature<T, E> signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T, E> executor, ResultHandler<E> resultHandler) {
        Page<?> page = local.get();
        if (page == null) {
            return getDelegate().execute(signature, methodArgs, sql, sqlArgs, executor, resultHandler);
        }
        if (page.getRecords() != null) {
            throw new IllegalStateException("the closure of DaoHelper.page can only contain one query");
        }
        if (signature.getType() != StatementType.SELECT) {
            throw new IllegalArgumentException(signature.getType() + " statement does not support paging");
        }
        if (!Collection.class.isAssignableFrom(signature.getReturnType())) {
            throw new IllegalArgumentException("single row query does not support paging");
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

        Select finalSelect = select;
        return getDelegate().withConnection(connection -> {
            try {
                if (!page.isSkipCount() && !page.isOrderByOnly()) {
                    long count = execCountSql(connection, finalSelect, signature, sql, sqlArgs);

                    page.setTotal(count);
                    int offset = (page.getPageIndex() - 1) * page.getPageSize();
                    logger.debug("count: {}, offset:{}", count, page.getOffset());
                    if (count == 0L || offset >= count) {
                        page.setRecords(new ArrayList());
                        return null;
                    }
                }

                SqlAndParamIndexPair pair = optimizeSql(finalSelect,
                        sql,
                        !page.isOrderByOnly(),
                        !page.isOrderByOnly(),
                        page.getOrderBy() != null,
                        true);
                String pageSql = DatabaseDialect.getDialect(connection.getMetaData().getDatabaseProductName())
                        .getPageSql(page, pair.sql);
                for (Integer idx : pair.indexOfParameterToSkip) {
                    sqlArgs.remove(idx - 1);
                }
                T result = getDelegate().execute(signature, methodArgs, pageSql, sqlArgs, executor, resultHandler);
                page.setRecords(new ArrayList(((Collection<?>) result)));

                return result;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    /**
     * 执行count查询
     *
     * @return
     */
    private static long execCountSql(Connection connection, Select select, SqlSignature<?, ?> signature, String originalSql, List<Object> sqlArgs) throws SQLException {
        String countSql;
        SqlAndParamIndexPair pair = optimizeSql(select, originalSql, true, true, true, true);
        countSql = "select count(*) from(\n" + pair.sql + "\n) as _cot";
        if (logger.isDebugEnabled()) {
            LogHelper.logSql(logger, countSql);
            LogHelper.logArgs(logger, sqlArgs);
        }

        try (PreparedStatement stmt = connection.prepareStatement(countSql)) {
            for (int i = 0; i < sqlArgs.size(); i++) {
                if (pair.indexOfParameterToSkip.contains(i + 1)) {
                    continue;
                }
                stmt.setObject(i + 1, sqlArgs.get(i));
            }
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private static class SqlAndParamIndexPair {
        String sql;
        SortedSet<Integer> indexOfParameterToSkip = new TreeSet<>(Comparator.reverseOrder());
    }

    public static SqlAndParamIndexPair optimizeSql(Select select,
                                                   String originalSql,
                                                   boolean removeLimit,
                                                   boolean removeOffset,
                                                   boolean removeOrderBy,
                                                   boolean removeForUpdate) {
        SqlAndParamIndexPair pair = new SqlAndParamIndexPair();
        pair.sql = originalSql;
        if (select == null || !(select.getSelectBody() instanceof PlainSelect)) {
            return pair;
        }
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        boolean forUpdate = plainSelect.isForUpdate();
        Limit limit = plainSelect.getLimit();
        Offset offset = plainSelect.getOffset();
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        ExpressionVisitorAdapter getParamIndexVisitor = new ExpressionVisitorAdapter() {
            @Override
            public void visit(JdbcParameter parameter) {
                pair.indexOfParameterToSkip.add(parameter.getIndex());
            }
        };
        if (removeForUpdate) {
            plainSelect.setForUpdate(false);
        }
        if (removeLimit && limit != null) {
            plainSelect.setLimit(null);
            limit.getRowCount().accept(getParamIndexVisitor);
        }
        if (removeOffset && offset != null) {
            plainSelect.setOffset(null);
            offset.getOffset().accept(getParamIndexVisitor);
        }
        if (removeOrderBy && orderByElements != null) {
            for (OrderByElement element : orderByElements) {
                element.getExpression().accept(getParamIndexVisitor);
            }
        }
        pair.sql = select.toString();
        plainSelect.setForUpdate(forUpdate);
        plainSelect.setLimit(limit);
        plainSelect.setOffset(offset);
        plainSelect.setOrderByElements(orderByElements);
        return pair;
    }
}
