package io.github.afezeria.freedao;

/**
 * 结果类型处理器
 * <p>
 * 如果需要对返回结果进行处理请使用自定义类并添加 {@code public static Object handleResult(Object result,Class<?> clazz)}
 * 方法，如果结果处理器设置为当前类型则不会对驱动结果进行处理
 * </p>
 * <p>
 * 参数为枚举类型且编译期可知时，默认将使用{@link DefaultEnumTypeHandler}处理
 * </p>
 */
public class ResultTypeHandler {
    public static Object handleResult(Object result, Class<?> clazz) {
        return result;
    }
}
