package com.github.afezeria.freedao.spring.runtime;

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
        public String get(String[] names) {
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
        public String get(String[] names) {
            if (names.length == 1) {
                return names[0];
            }
            return names[random.nextInt(names.length - 1)];
        }
    };

    public static DataSourceSelectStrategy DEFAULT = POLLING;


    /**
     * 获取数据源名称
     *
     * @param names 符合前缀的数据源名称数组
     * @return
     */
    abstract public String get(String[] names);
}
