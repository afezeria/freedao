package test.java;

import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
public interface BaseDao<T> {
    List<T> all();

    int insert(T entity);
}
