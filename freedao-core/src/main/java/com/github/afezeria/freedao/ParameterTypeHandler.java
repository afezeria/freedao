package com.github.afezeria.freedao;

/**
 * 将字段类型映射到数据库类型
 * <p></p>
 * 子类必须包含和handle方法签名相同的静态方法
 */
public class ParameterTypeHandler {
    public static Object handle(Object arg) {
        return arg;
    }
}
