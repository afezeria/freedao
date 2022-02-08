package test.java.crud.update;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

/**
 */
@Dao(crudEntity = PersonUpdate.class)
public interface PersonUpdateDao {

    int update(@NotNull PersonUpdate entity);
}
