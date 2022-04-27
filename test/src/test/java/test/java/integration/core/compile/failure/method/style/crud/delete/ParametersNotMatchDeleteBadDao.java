package test.java.integration.core.compile.failure.method.style.crud.delete;

import io.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ParametersNotMatchDeleteBadDao {
    int delete(@NotNull String str);
}
