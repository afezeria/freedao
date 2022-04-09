package com.github.afezeria.freedao.classic.runtime;

import com.github.afezeria.freedao.classic.runtime.context.PaginationQueryContext;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Supplier;

import static com.github.afezeria.freedao.classic.runtime.FreedaoGlobalConfiguration.optimizeCountSql;

/**
 * @author afezeria
 */
public class DaoHelper {

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
     * @param ds
     * @param supplier
     * @param <T>
     * @return
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

    public static <E> Page<E> pagination(int pageIndex, int pageSize, Supplier<Collection<E>> closure) {
        return pagination(pageIndex, pageSize, optimizeCountSql, closure);
    }

    public static <E> Page<E> pagination(int pageIndex, int pageSize, boolean optimizeCountSql, Supplier<Collection<E>> closure) {
        if (pageIndex < 1) {
            throw new IllegalArgumentException("pageIndex must greater than 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must greater than 0");
        }
        Page<E> page = new Page<>();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setOptimizeCountSql(optimizeCountSql);
        PaginationQueryContext.local.set(page);
        try {
            Collection<E> res = closure.get();
            if (res != null) {
                page.setRecords(new ArrayList<>(res));
            } else {
                page.setRecords(new ArrayList<>());
            }
            return page;
        } finally {
            PaginationQueryContext.local.remove();
        }
    }

    public static void main(String[] args) {
        List<String> l = new ArrayList<>();
//        Page<String> with = with(1, 1, () -> l);
        System.out.println(List.class.isAssignableFrom(Collection.class));
        System.out.println(Collection.class.isAssignableFrom(List.class));
        System.out.println(Collection.class.isAssignableFrom(Set.class));
    }
}
