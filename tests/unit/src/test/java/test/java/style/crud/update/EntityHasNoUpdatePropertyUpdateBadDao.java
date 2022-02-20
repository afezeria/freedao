package test.java.style.crud.update;

import com.github.afezeria.freedao.annotation.Dao;
import test.OnePropertyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = OnePropertyEntity.class)
public interface EntityHasNoUpdatePropertyUpdateBadDao {
    int update(OnePropertyEntity entity);
}
