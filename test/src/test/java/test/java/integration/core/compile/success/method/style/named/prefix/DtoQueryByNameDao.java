package test.java.integration.core.compile.success.method.style.named.prefix;

import io.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;
import test.PersonIdAndNameDto;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface DtoQueryByNameDao {

    List<PersonIdAndNameDto> dtoQueryByName(@NotNull String name);
}
