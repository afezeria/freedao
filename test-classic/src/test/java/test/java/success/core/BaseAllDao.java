package test.java.success.core;

import java.util.List;

/**
 * @author afezeria
 */
public interface BaseAllDao<T> {
    List<T> list(T entity);
}
