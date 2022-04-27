package io.github.afezeria.freedao.classic.runtime;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认的自动填充器，用于将数据库生成的字段填充到bean中，只能用于AutoFill.before=false的场景
 */
public class DbGenerator {

    /**
     * 生成用于填充自动填充字段的值
     *
     * @param obj  当AutoFill.before为true时，为待插入对象，当before为false时，为ResultSet
     * @param name 字段名称
     * @param type 字段类型
     */
    public static Object gen(Object obj, String name, Class<?> type) throws SQLException {
        if (type == Object.class) {
            return ((ResultSet) obj).getObject(name);
        } else {
            return ((ResultSet) obj).getObject(name, type);
        }
    }
}