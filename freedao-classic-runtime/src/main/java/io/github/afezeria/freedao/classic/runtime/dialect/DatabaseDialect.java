package io.github.afezeria.freedao.classic.runtime.dialect;

import io.github.afezeria.freedao.classic.runtime.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author afezeria
 */
public abstract class DatabaseDialect {

    private static final Map<String, DatabaseDialect> map = new HashMap<>();

    static {
        for (DatabaseDialect dialect : ServiceLoader.load(DatabaseDialect.class)) {
            map.put(dialect.getProductName(), dialect);
        }
    }

    abstract public String getProductName();

    public static DatabaseDialect getDialect(String name) {
        DatabaseDialect dialect = map.get(name);
        if (dialect == null) {
            throw new RuntimeException("unsupported dialect:" + name);
        }
        return dialect;
    }

    abstract public String getPageSql(Page<?> page, String sql);
}
