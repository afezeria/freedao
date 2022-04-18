package test.java.integration.core.compile.failure.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.DtoNoCommonFieldWithPerson;
import test.Person;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface DtoQueryNoCommonFieldBadDao {

    List<DtoNoCommonFieldWithPerson> dtoQueryByName(@NotNull String name);
}
