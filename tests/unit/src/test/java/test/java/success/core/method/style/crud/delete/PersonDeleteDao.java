package test.java.success.core.method.style.crud.delete;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface PersonDeleteDao {

    int delete(@NotNull Long id);
}
