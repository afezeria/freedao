package test.java.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
@Dao(crudEntity = PersonInsertSelective.class)
public interface PersonInsertSelectiveDao {

    int insertSelective(@NotNull PersonInsertSelective entity);
}
