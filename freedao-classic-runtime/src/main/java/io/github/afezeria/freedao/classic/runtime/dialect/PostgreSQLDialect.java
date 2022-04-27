package io.github.afezeria.freedao.classic.runtime.dialect;

import io.github.afezeria.freedao.classic.runtime.Page;

/**
 * @author afezeria
 */
public class PostgreSQLDialect extends DatabaseDialect {
    @Override
    public String getProductName() {
        return "PostgreSQL";
    }

    @Override
    public String getPageSql(Page<?> page, String sql) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ( \n").append(sql).append("\n ) as _tmp ");
        if (page.getOrderBy() != null) {
            builder.append(" order by ").append(page.getOrderBy());
        }
        if (!page.isOrderByOnly()) {
            builder.append(" limit ").append(page.getLimit()).append(" offset ").append(page.getOffset());
        }
        return builder.toString();
    }
}
