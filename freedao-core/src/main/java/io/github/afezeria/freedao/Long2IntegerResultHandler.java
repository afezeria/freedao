package io.github.afezeria.freedao;

/**
 * @author afezeria
 */
public class Long2IntegerResultHandler {
    public static Integer handleResult(Object result, Class<?> clazz) {
        if (result == null) {
            return null;
        }
        if (result instanceof Long) {
            return ((Long) result).intValue();
        }
        if (result instanceof Integer) {
            return (Integer) result;
        }
        throw new RuntimeException("Cannot cast " + result.getClass() + ":" + result + "to integer");
    }
}
