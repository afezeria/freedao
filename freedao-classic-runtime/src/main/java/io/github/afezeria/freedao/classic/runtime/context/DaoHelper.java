package io.github.afezeria.freedao.classic.runtime.context;

import io.github.afezeria.freedao.classic.runtime.DS;
import io.github.afezeria.freedao.classic.runtime.DataSourceContextHolder;
import io.github.afezeria.freedao.classic.runtime.Page;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author afezeria
 */
public class DaoHelper {

    /**
     * 设置临时上下文参数
     */
    public static <T> T withContextParameter(String key, Object value, Supplier<T> block) {
        ParameterContext.ParameterMap parameterMap =
                Objects.requireNonNull(ParameterContext.local.get(), "ParameterContext not initialized");
        parameterMap.put(key, value);
        try {
            return block.get();
        } finally {
            parameterMap.reset();
        }
    }

    /**
     * 设置临时上下文参数
     */
    public static <T> T withContextParameters(Map<String, Object> params, Supplier<T> block) {
        ParameterContext.ParameterMap parameterMap =
                Objects.requireNonNull(ParameterContext.local.get(), "ParameterContext not initialized");
        parameterMap.putAll(params);
        try {
            return block.get();
        } finally {
            parameterMap.reset();
        }
    }

    /**
     * {@link DaoHelper#ds(DS, Supplier)}
     */
    public static <T> T ds(String name, Supplier<T> supplier) {
        return ds(name, true, supplier);
    }

    /**
     * {@link DaoHelper#ds(DS, Supplier)}
     */
    public static <T> T ds(String name, boolean isPrefix, Supplier<T> supplier) {
        return ds(new DS() {
            @Override
            public String value() {
                return name;
            }

            @Override
            public boolean prefix() {
                return isPrefix;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        }, supplier);
    }

    /**
     * 切换数据源，暂时只支持spring-boot
     *
     * @param ds {@link DS}
     */
    public static <T> T ds(DS ds, Supplier<T> supplier) {
        DS outer = DataSourceContextHolder.get();
        if (outer != null) {
            if (outer.prefix() == ds.prefix() && Objects.equals(outer.value(), ds.value())) {
                return supplier.get();
            }
        }
        try {
            DataSourceContextHolder.set(ds);
            return supplier.get();
        } finally {
            DataSourceContextHolder.set(outer);
        }
    }

    public static <E> Page<E> page(int pageIndex, int pageSize, Supplier<Collection<E>> closure) {
        return page(Page.of(pageIndex, pageSize), closure);
    }

    @SuppressWarnings("unchecked")
    public static <E> Page<E> page(Page<?> page, Supplier<Collection<E>> closure) {
        if (PageQueryContext.local.get() != null) {
            throw new RuntimeException("paging conditions cannot be set repeatedly");
        }
        PageQueryContext.local.set(page);
        try {
            closure.get();
//            if (res != null) {
//                page.setRecords(new ArrayList(res));
//            } else {
//                page.setRecords(new ArrayList<>());
//            }
            return (Page<E>) page;
        } finally {
            PageQueryContext.local.remove();
        }
    }
}
