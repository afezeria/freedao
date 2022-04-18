package test.java.integration.core.compile.success.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface QueryByNameDao {

    List<Person> queryByName(@NotNull String name);
}
