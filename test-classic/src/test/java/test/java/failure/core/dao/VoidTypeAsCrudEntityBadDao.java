package test.java.failure.core.dao;

import com.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao(crudEntity = int.class)
public interface VoidTypeAsCrudEntityBadDao {
}
