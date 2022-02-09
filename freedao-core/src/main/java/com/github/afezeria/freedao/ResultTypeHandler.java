package com.github.afezeria.freedao;

/**
 * 处理数据库驱动返回值类型和对象字段类型的映射
 * <p>
 * 该类实际上只是个占位符
 * <p>
 * 默认情况下会假设数据库驱动返回的值的类型和期望的类型一致，会直接进行赋值操作，而不会应用typeHandler
 * <p>
 * 自定义ResultTypeHandler必须包含名为handle的静态方法，参数个数为一，类型为java.lang.Object，返回类型为字段类型
 */
public class ResultTypeHandler {
    public static Object handle(Object result) {
        return result;
    }
}
