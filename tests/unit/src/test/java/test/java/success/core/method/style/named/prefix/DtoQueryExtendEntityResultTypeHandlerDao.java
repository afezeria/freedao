package test.java.success.core.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;
import test.PersonStringAgeDto;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface DtoQueryExtendEntityResultTypeHandlerDao {

    List<PersonStringAgeDto> dtoQueryByName(@NotNull String name);
}