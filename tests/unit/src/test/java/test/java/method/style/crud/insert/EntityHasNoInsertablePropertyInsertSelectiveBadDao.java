package test.java.method.style.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import test.EmptyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = EmptyEntity.class)
public interface EntityHasNoInsertablePropertyInsertSelectiveBadDao {
    int insertSelective(EmptyEntity entity);
}
