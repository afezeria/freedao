package test.java.method.style.crud.delete;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.EmptyEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = EmptyEntity.class)
public interface EntityWithoutPrimaryKeyDeleteBadDao {
    int delete(@NotNull Long id);
}
