package com.github.afezeria.freedao.runtime.classic.context;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author afezeria
 */
public class DaoContextParameterUtil {
    public static <T> T with(String key, Object value, Supplier<T> block) {
        ParameterContext.ParameterMap parameterMap =
                Objects.requireNonNull(ParameterContext.local.get(), "ParameterContext not initialized");
        parameterMap.put(key, value);
        T t = block.get();
        parameterMap.reset();
        return t;
    }
}
