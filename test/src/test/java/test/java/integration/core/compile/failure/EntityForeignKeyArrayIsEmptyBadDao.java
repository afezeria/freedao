package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao(crudEntity = EntityForeignKeyArrayIsEmpty.class)
public interface EntityForeignKeyArrayIsEmptyBadDao {
}
