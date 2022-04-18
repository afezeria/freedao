package test.java.integration.core.compile.failure.dao;

import com.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao(crudEntity = String.class)
public interface EntityIsStringBadDao {
}
