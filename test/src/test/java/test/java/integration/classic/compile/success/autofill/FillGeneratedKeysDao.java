package test.java.integration.classic.compile.success.autofill;

import io.github.afezeria.freedao.annotation.Dao;
import test.DbGeneratedKeyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = DbGeneratedKeyEntity.class)
public interface FillGeneratedKeysDao {
    int insert(DbGeneratedKeyEntity entity);
}
