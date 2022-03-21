package test.java.success.classic.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import test.DbGeneratedKeyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = DbGeneratedKeyEntity.class)
public interface FillGeneratedKeysDao {
    int insert(DbGeneratedKeyEntity entity);
}
