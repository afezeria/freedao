package test.java.jpa;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
@Dao(crudEntity = PersonQueryByName.class)
public interface PersonQueryByIdDao {

    PersonQueryByName queryOneById(@NotNull Long id);
}
