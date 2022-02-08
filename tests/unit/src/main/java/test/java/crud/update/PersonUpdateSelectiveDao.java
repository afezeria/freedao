package test.java.crud.update;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

/**
 */
@Dao(crudEntity = PersonUpdateSelective.class)
public interface PersonUpdateSelectiveDao {

    int updateSelective(@NotNull PersonUpdateSelective entity);
}
