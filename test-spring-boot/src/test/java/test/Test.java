package test;

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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author afezeria
 */
public class Test {
    public static Logger logger = LoggerFactory.getLogger(Test.class);

    /**
     * 优化count sql 去除没有出现在
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
            return original;
        }
        PlainSelect body = (PlainSelect) ((Select) stmt).getSelectBody();
        //使用加锁语句进行分页查询时抛出异常
        if (body.isForUpdate()) {
            throw new RuntimeException("sql contain locking clause, cannot convert to pagination query");
        }
        //有distinct时不优化
        if (body.getDistinct() != null) {
            return original;
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
            return original;
        }

        //sql包含group by或者having时关闭优化
        if (body.getHaving() != null || body.getGroupBy() != null) {
            return original;
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
            return original;
        }
        //where关键字后存在未指定表名的列时不优化
        if (!visitor.allColumnSpecifyTable) {
            return original;
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

    public static void main(String[] args) throws JSQLParserException {
        //language=SQL
        optimizeSql("""
                SELECT dg.id as group_id,dg.group_name,dg.share_type ,'abc' as c,count(b.*) as cot
                FROM t_device_group as dg
                LEFT JOIN t_group_mid_device as gm
                LEFT JOIN t_group_mid_device on dg.id = gm.group_id
                left join (select * from t_device) b
                left join (select * from t_device) d
                WHERE dg.delete_flag = 0
                    and dg.group_name like CONCAT('%','','%')
                    and gm.device_id like CONCAT('%',?,'%')
                    and dg.share_type = ?
                and dg.mch_id = ?
                order BY dg.id desc ,1
                """);

    }
}
