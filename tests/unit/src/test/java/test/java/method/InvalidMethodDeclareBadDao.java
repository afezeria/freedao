package test.java.method;

import com.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao
public interface InvalidMethodDeclareBadDao {
    int abc();
}
