package com.github.afezeria.freedao;

/**
 * 处理数据库驱动返回值类型和对象字段类型的映射
 * <p>
 * 该类实际上只是个占位符
 * <p>
 * 默认情况下会假设数据库驱动返回的值的类型和期望的类型一致，会直接进行赋值操作，而不会应用typeHandler
 * <p>
 * 子类必须包含和handle方法签名相同的静态方法
 */
public class ResultTypeHandler {
    public static Object handle(Object result) {
        return result;
    }
}
