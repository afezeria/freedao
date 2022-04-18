package test.java.integration.core.compile.success.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface RemoveByNameDao {

    Integer removeByName(@NotNull String name);
}
