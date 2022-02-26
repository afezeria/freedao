package test.java.failure.core.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
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
