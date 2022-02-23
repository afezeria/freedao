package test.java.success.core;

import java.util.List;

/**
 * @author afezeria
 */
public interface BaseDao<T> {
    List<T> all();

    int insert(T entity);
}
