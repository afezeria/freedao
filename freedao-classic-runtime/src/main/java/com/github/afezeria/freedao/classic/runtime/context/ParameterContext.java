package com.github.afezeria.freedao.classic.runtime.context;

import com.github.afezeria.freedao.classic.runtime.SqlSignature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author afezeria
 */
public class ParameterContext extends DaoContext {

    public ParameterContext(Map<String, Object> map) {
        if (map != null) {
            initMap.putAll(map);
        }
        local = ThreadLocal.withInitial(() -> new ParameterMap(initMap));
    }

    private final Map<String, Object> initMap = new HashMap<>();
    protected static ThreadLocal<ParameterMap> local;

    @Override
    public Object[] buildSql(SqlSignature signature, Object[] args, Function<Object[], Object[]> sqlBuilder) {
        ParameterMap map = local.get();

        Object[] arr = new Object[args.length + 1];
        System.arraycopy(args, 0, arr, 0, args.length);
        arr[args.length] = map;

        return getDelegate().buildSql(signature, arr, sqlBuilder);
    }

    static class ParameterMap extends HashMap<String, Object> {

        private final Map<String, Object> init;

        private final Map<String, Object> tmp = new HashMap<>();

        public ParameterMap(Map<String, Object> init) {
            this.init = init;
        }


        @Override
        public Object get(Object key) {
            Object value = tmp.get(key);
            if (value == null) {
                value = init.get(key);
            }
            if (value == null) {
                return null;
            }
            if (value instanceof Supplier) {
                return ((Supplier<?>) value).get();
            } else {
                return value;
            }
        }

        @Override
        public Object put(String key, Object value) {
            return tmp.put(key, value);
        }

        public void reset() {
            tmp.clear();
        }
    }
}
