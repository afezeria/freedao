package test.java.failure.core.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface DtoQueryOneNotReturnCustomJavaBeanBadDao {

    String dtoQueryOneByName(@NotNull String name);
}
