package test.java.success.core.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;
import test.PersonIdAndNameDto;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface DtoQueryOneByNameDao {

    PersonIdAndNameDto dtoQueryOneByName(@NotNull String name);
}
