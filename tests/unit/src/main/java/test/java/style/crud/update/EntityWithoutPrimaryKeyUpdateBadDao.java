package test.java.style.crud.update;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.EmptyEntity;
import test.PersonLog;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonLog.class)
public interface EntityWithoutPrimaryKeyUpdateBadDao {
    int update(@NotNull PersonLog entity);
}
