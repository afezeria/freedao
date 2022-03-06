package test.java.success.core;

/**
 * @author afezeria
 */
public interface BaseInsertDao<T> {
    int insert(T entity);
}
