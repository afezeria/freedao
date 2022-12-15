package io.github.afezeria.freedao.classic.runtime;

import io.github.afezeria.freedao.classic.runtime.context.DaoContext;

import java.lang.reflect.Constructor;

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
            ((AbstractDao) instance).setContext(context);
            return (T) instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
