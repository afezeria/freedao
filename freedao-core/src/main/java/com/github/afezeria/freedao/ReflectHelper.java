package com.github.afezeria.freedao;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class ReflectHelper {
    private static final Map<Class<?>, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    public static Object call(Object receiver, String name) {
        if (receiver == null) {
            throw new NullPointerException("Cannot invoke " + name + " on null");
        }
        if (receiver instanceof Map<?, ?>) {
            if (!name.equals("size")) {
                throw new RuntimeException("property " + name + " not found");
            }
            return ((Map<?, ?>) receiver).size();
        } else if (receiver instanceof List<?>) {
            if (!name.equals("size")) {
                throw new RuntimeException("property " + name + " not found");
            }
            return ((List<?>) receiver).size();
        }
        Method method = METHOD_CACHE.computeIfAbsent(receiver.getClass(), c -> {
            try {
                return c.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            return method.invoke(receiver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class A {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        A a = new A();
        a.name = "cc";
        System.out.println(call(a, "name"));
    }
}
