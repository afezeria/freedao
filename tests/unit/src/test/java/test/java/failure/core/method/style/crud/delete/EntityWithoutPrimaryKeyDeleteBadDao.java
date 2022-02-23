package test.java.failure.core.method.style.crud.delete;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.PersonWithoutPrimaryKey;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonWithoutPrimaryKey.class)
public interface EntityWithoutPrimaryKeyDeleteBadDao {
    int delete(@NotNull Long id);
}
