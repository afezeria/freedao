package com.github.afezeria.freedao;

/**
 * 参数类型处理器，在拼接sql时调用，将java对象转换成数据库驱动可处理的对象
 * <p>
 * 如果需要对参数进行处理请自定义类型并实现{@code public static Object handleParameter(Object arg)}方法,
 * 参数处理器为当前类型时参数对象将被直接传递给数据库驱动
 * </p>
 * <p>
 * 参数为枚举类型且编译期可知时，默认将使用{@link com.github.afezeria.freedao.DefaultEnumTypeHandler}处理
 * </p>
 */
public class ParameterTypeHandler {
    public static Object handleParameter(Object arg) {
        return arg;
    }
}
