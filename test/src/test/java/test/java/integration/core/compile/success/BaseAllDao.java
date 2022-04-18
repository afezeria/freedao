package test.java.integration.core.compile.success;

import java.util.List;

/**
 * @author afezeria
 */
public interface BaseAllDao<T> {
    List<T> list(T entity);
}
