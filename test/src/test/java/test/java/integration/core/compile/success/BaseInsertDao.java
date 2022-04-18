package test.java.integration.core.compile.success;

/**
 * @author afezeria
 */
public interface BaseInsertDao<T> {
    int insert(T entity);
}
