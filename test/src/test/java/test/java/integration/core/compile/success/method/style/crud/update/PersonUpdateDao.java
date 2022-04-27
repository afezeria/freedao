package test.java.integration.core.compile.success.method.style.crud.update;

import io.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface PersonUpdateDao {

    int update(@NotNull Person entity);
}
