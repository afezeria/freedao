package test.java.integration.core.compile.failure.method.style.crud.update;

import io.github.afezeria.freedao.annotation.Dao;
import test.OnePropertyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = OnePropertyEntity.class)
public interface EntityHasNoUpdatePropertyUpdateBadDao {
    int update(OnePropertyEntity entity);
}
