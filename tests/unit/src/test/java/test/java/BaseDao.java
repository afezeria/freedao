package test.java;

import java.util.List;

/**
 * @author afezeria
 */
public interface BaseDao<T> {
    List<T> all();

    int insert(T entity);
}
