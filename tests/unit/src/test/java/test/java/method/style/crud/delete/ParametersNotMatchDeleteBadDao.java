package test.java.method.style.crud.delete;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ParametersNotMatchDeleteBadDao {
    int delete(@NotNull String str);
}
