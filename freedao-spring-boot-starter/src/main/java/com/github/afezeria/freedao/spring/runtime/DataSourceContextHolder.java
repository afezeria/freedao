package com.github.afezeria.freedao.spring.runtime;

/**
 * @author afezeria
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<DS> CONTEXT = new ThreadLocal<>();

    public static void set(DS ds) {
        CONTEXT.set(ds);
    }

    public static DS get() {
        return CONTEXT.get();
    }
}