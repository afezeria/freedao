package test.java.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

/**
 */
@Dao(crudEntity = PersonInsert.class)
public interface PersonInsertDao {

    int insert(@NotNull PersonInsert entity);
}
