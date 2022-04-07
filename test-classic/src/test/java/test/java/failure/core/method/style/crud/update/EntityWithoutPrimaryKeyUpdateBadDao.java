package test.java.failure.core.method.style.crud.update;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.PersonWithoutPrimaryKey;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonWithoutPrimaryKey.class)
public interface EntityWithoutPrimaryKeyUpdateBadDao {
    int update(@NotNull PersonWithoutPrimaryKey entity);
}
