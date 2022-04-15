package com.github.afezeria.freedao.classic.runtime.dialect;

/**
 * @author afezeria
 */
public class MysqlDialect extends PostgreSQLDialect {
    @Override
    public String getProductName() {
        return "mysql";
    }
}
