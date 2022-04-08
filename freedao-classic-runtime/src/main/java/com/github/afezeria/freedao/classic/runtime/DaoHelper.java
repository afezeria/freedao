package com.github.afezeria.freedao.classic.runtime;

import com.github.afezeria.freedao.classic.runtime.context.PaginationQueryContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.github.afezeria.freedao.classic.runtime.FreedaoGlobalConfiguration.optimizeCountSql;

/**
 * @author afezeria
 */
public class DaoHelper {

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
