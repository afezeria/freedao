package com.github.afezeria.freedao.classic.runtime.context;

import com.github.afezeria.freedao.StatementType;
import com.github.afezeria.freedao.classic.runtime.LogHelper;
import com.github.afezeria.freedao.classic.runtime.Page;
import com.github.afezeria.freedao.classic.runtime.SqlExecutor;
import com.github.afezeria.freedao.classic.runtime.SqlSignature;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
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
public class PaginationQueryContext extends DaoContext {

    private static final Logger logger = LoggerFactory.getLogger(PaginationQueryContext.class);
    protected static ThreadLocal<Page<?>> local = new ThreadLocal<>();

    @Override
    public <T> T execute(SqlSignature signature, Object[] methodArgs, String sql, List<Object> sqlArgs, SqlExecutor<T> executor) {
        return getDelegate().withTx(connection -> {
            Page<?> page = local.get();
            if (page == null) {
                return getDelegate().execute(signature, methodArgs, sql, sqlArgs, executor);
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
            return getDelegate().execute(signature, methodArgs, pagingSql, sqlArgs, executor);
        });
    }

    private static final ConcurrentHashMap<SqlSignature, Boolean> disableOptimizeSqlFlag = new ConcurrentHashMap<>();

    private static long execCountSql(Connection connection, SqlSignature signature, String originalSql, List<Object> sqlArgs) {

        String countSql = null;
//        if (FreedaoGlobalConfiguration.optimizeCountSql) {
//            Boolean flag = disableOptimizeSqlFlag.getOrDefault(signature, true);
//            if (flag) {
//                try {
//                    countSql = optimizeSql(originalSql);
//                } catch (JSQLParserException e) {
//                    disableOptimizeSqlFlag.put(signature, false);
//                    logger.error("failed to optimize sql", e);
//                } catch (DisableOptimizeException e) {
//                    disableOptimizeSqlFlag.put(signature, false);
//                }
//            }
//        }
//        if (countSql == null) {
//            //language = SQL
//            countSql = "select count(*) from (" + originalSql + ") as _cot";
//        }
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

    /**
     * 优化count sql
     * todo left join好像没法优化掉
     *
     * @param original
     * @return
     */
    public static String optimizeSql(String original) throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse(original);
        if (!(stmt instanceof Select)) {
            throw new RuntimeException("invalid statement type");
        }
        Select select = (Select) stmt;
        //有with表达式时不尝试优化
        if (select.getWithItemsList() != null) {
            throw DisableOptimizeException.instance;
        }
        PlainSelect body = (PlainSelect) ((Select) stmt).getSelectBody();
        //使用加锁语句进行分页查询时抛出异常
        if (body.isForUpdate()) {
            throw new RuntimeException("sql contain locking clause, cannot convert to pagination query");
        }
        //有distinct时不优化
        if (body.getDistinct() != null) {
            throw DisableOptimizeException.instance;
        }
        //当所有顶层join的on表达式中有列未指定表名时不进行优化
        List<Join> joins = body.getJoins();
        JoinOnExpressionCheckVisitor joinCheckVisitor = new JoinOnExpressionCheckVisitor();
        for (Join join : joins) {
            for (Expression expression : join.getOnExpressions()) {
                expression.accept(joinCheckVisitor);
            }
        }
        if (joinCheckVisitor.hasColumnNotSpecifyTable) {
            throw DisableOptimizeException.instance;
        }

        //sql包含group by或者having时关闭优化
        if (body.getHaving() != null || body.getGroupBy() != null) {
            throw DisableOptimizeException.instance;
        }

        //获取where关键字之后使用到的表名
        ExpressionCheckVisitor visitor = new ExpressionCheckVisitor();
        body.getWhere().accept(visitor);
        if (body.getOrderByElements() != null) {
            for (OrderByElement orderByElement : body.getOrderByElements()) {
                //如果有根据输出列的位置排序进行排序时关闭优化
                if (orderByElement.getExpression() instanceof LongValue) {
                    return original;
                }
                orderByElement.getExpression().accept(visitor);
            }
        }
        //where关键字后有子查询时不优化
        if (visitor.hasSubSelect) {
            throw DisableOptimizeException.instance;
        }
        //where关键字后存在未指定表名的列时不优化
        if (!visitor.allColumnSpecifyTable) {
            throw DisableOptimizeException.instance;
        }

        for (int i = joins.size() - 1; i >= 0; i--) {
            Join join = joins.get(i);
            FromItem rightItem = join.getRightItem();
            if (rightItem instanceof Table) {
                String alias = rightItem.getAlias() != null ? rightItem.getAlias().getName() : null;
                if (alias != null) {
                    if (!visitor.tableNameSet.contains(alias)) {
                        joins.remove(i);
                    }
                } else {
                    String name = ((Table) rightItem).getName();
                    if (!visitor.tableNameSet.contains(name)) {
                        joins.remove(i);
                    }
                }
            } else {
                String alias = rightItem.getAlias() != null ? rightItem.getAlias().toString() : null;
                if (alias != null) {
                    if (!visitor.tableNameSet.contains(alias)) {
                        joins.remove(i);
                    }
                }
            }
        }
        //将查询的selectItemList替换为count(*)
        body.withSelectItems(
                Collections.singletonList(
                        new SelectExpressionItem(
                                new Function()
                                        .withName("count")
                                        .withParameters(
                                                new ExpressionList()
                                                        .addExpressions(new AllColumns())
                                        )
                        )
                )
        );
        return select.toString();
    }

    private static class DisableOptimizeException extends RuntimeException {
        public static DisableOptimizeException instance = new DisableOptimizeException();
    }

    /**
     * 用于检查顶层join的on语句
     */
    private static class JoinOnExpressionCheckVisitor extends ExpressionVisitorAdapter {
        boolean hasColumnNotSpecifyTable = false;

        @Override
        public void visit(Column column) {
            if (column.getTable() == null) {
                hasColumnNotSpecifyTable = true;
            }
        }
    }

    /**
     * 用于检查顶层where关键字之后的表达式
     */
    private static class ExpressionCheckVisitor extends ExpressionVisitorAdapter {
        /**
         * 查找where关键字后所有使用到的表名，如果join列表中的所有表都被使用到了，则关闭优化
         */
        Set<String> tableNameSet = new HashSet<>();

        /**
         * 是否包含子查询，如果包含子查询则关闭优化
         */
        boolean hasSubSelect = false;

        /**
         * where关键字之后的字段是否都指定了表名，如果包含未指定表名的字段则关闭优化
         */
        boolean allColumnSpecifyTable = true;

        @Override
        public void visit(Column column) {
            if (column.getTable() != null) {
                tableNameSet.add(column.getTable().toString());
            } else {
                allColumnSpecifyTable = false;
            }
        }

        @Override
        public void visit(SubSelect subSelect) {
            hasSubSelect = true;
        }
    }
}
