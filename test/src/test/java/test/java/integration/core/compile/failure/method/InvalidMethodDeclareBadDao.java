package test.java.integration.core.compile.failure.method;

import io.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao
public interface InvalidMethodDeclareBadDao {
    int abc();
}
