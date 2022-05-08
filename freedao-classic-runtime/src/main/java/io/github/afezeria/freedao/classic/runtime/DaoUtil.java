package io.github.afezeria.freedao.classic.runtime;

import io.github.afezeria.freedao.classic.runtime.context.DaoContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @author afezeria
 */
public class DaoUtil {
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> daoClass, DaoContext context) {
        try {
            Class<?> implClass = Class.forName(daoClass.getCanonicalName() + "Impl");
            Constructor<?> constructor = implClass.getConstructor();
            Object instance = constructor.newInstance();
            Field field = implClass.getDeclaredField("__context__");
            field.setAccessible(true);
            field.set(instance, context);
            return (T) instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
