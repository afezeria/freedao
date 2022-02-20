package test.java.style.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import test.EmptyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = EmptyEntity.class)
public interface EntityHasNoInsertablePropertyInsertBadDao {
    int insert(EmptyEntity entity);
}
