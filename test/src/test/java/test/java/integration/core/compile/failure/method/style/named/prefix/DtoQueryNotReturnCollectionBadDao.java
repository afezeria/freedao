package test.java.integration.core.compile.failure.method.style.named.prefix;

import io.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;
import test.PersonIdAndNameDto;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface DtoQueryNotReturnCollectionBadDao {

    PersonIdAndNameDto dtoQueryByName(@NotNull String name);
}
