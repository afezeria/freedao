package com.github.afezeria.freedao;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的枚举类型处理器
 * <p>
 * 如果没有给枚举类型的字段指定类型处理器则使用该处理器处理
 * </p>
 * <p>
 * 作为参数处理器时将枚举转换成字符串，字符串的为枚举的name属性的值
 * </p>
 * <p>
 * 作为结果处理器时接受字符串，根据枚举的name属性返回匹配的枚举类，未找到时抛出IllegalArgumentException
 * </p>
 *
 * @author afezeria
 */
public class DefaultEnumTypeHandler {
    private static final ConcurrentHashMap<Class<? extends Enum<?>>, Enum<?>[]> cache = new ConcurrentHashMap<>();

    public static Object handleParameter(Enum<?> arg) {
        return arg.name();
    }

    public static Object handleResult(Object result, Class<?> clazz) {
        if (result == null) {
            return null;
        }
        String name = ((String) result);
        Enum<?>[] enums = cache.get(clazz);
        if (enums == null) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> clazz1 = (Class<? extends Enum<?>>) clazz;
            enums = clazz1.getEnumConstants();
            cache.put(clazz1, enums);
        }
        for (Enum<?> anEnum : enums) {
            if (anEnum.name().equals(name)) {
                return anEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant " + clazz.getCanonicalName() + "." + name);
    }
}
