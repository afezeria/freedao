package com.github.afezeria.freedao.spring.runtime;

import com.github.afezeria.freedao.classic.runtime.DS;
import com.github.afezeria.freedao.classic.runtime.DataSourceContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author afezeria
 */
public abstract class DataSourceSelectStrategy {
    public static DataSourceSelectStrategy POLLING = new DataSourceSelectStrategy() {
        private final ThreadLocal<Map<String[], Integer>> mapThreadLocal = ThreadLocal.withInitial(HashMap::new);

        @Override
        public String apply(Map<String, String[]> prefixMap) {
            String[] names = getSelectableDatasourceName(prefixMap);
            if (names == null) {
                return null;
            }
            if (names.length == 1) {
                return names[0];
            }
            Map<String[], Integer> map = mapThreadLocal.get();
            Integer idx = map.get(names);
            if (idx == null) {
                idx = 0;
            }
            if (idx > names.length) {
                idx = 0;
            }
            map.put(names, idx + 1);
            return names[idx];
        }
    };

    public static DataSourceSelectStrategy RANDOM = new DataSourceSelectStrategy() {
        private final Random random = new Random();

        @Override
        public String apply(Map<String, String[]> prefixMap) {
            String[] names = getSelectableDatasourceName(prefixMap);
            if (names == null) {
                return null;
            }
            if (names.length == 1) {
                return names[0];
            }
            return names[random.nextInt(names.length - 1)];
        }
    };

    public static DataSourceSelectStrategy DEFAULT = POLLING;

    abstract public String apply(Map<String, String[]> prefixMap);

    public String[] getSelectableDatasourceName(Map<String, String[]> prefixMap) {
        DS ds = DataSourceContextHolder.get();
        if (ds == null) {
            return null;
        }
        String[] arr;
        arr = prefixMap.get(ds.value());
        if (arr == null) {
            throw new IllegalStateException("Cannot find datasource with prefix " + ds.value());
        }
        return arr;
    }
}
