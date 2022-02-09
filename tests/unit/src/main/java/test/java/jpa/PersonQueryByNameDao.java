package test.java.jpa;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = PersonQueryByName.class)
public interface PersonQueryByNameDao {

    List<PersonQueryByName> queryByName(@NotNull String name);
}
