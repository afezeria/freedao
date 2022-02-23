package test.java.failure.core.method;

import com.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao
public interface InvalidMethodDeclareBadDao {
    int abc();
}
