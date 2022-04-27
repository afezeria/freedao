package test.java.integration.core.compile.success.method.style.named.prefix;

import io.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface FindByNameDao {

    List<Person> findByName(@NotNull String name);
}
