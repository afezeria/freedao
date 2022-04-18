package test.java.integration.core.compile.failure.method.style.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import test.PersonWithoutInsertableProperty;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonWithoutInsertableProperty.class)
public interface EntityHasNoInsertablePropertyInsertBadDao {
    int insert(PersonWithoutInsertableProperty entity);
}
