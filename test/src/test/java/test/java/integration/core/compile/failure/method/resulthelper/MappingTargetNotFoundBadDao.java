package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.Mapping;
import io.github.afezeria.freedao.annotation.ResultMappings;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface MappingTargetNotFoundBadDao {
    @ResultMappings(
            value = {
                    @Mapping(
                            source = "",
                            target = "abc"
                    )
            }
    )
    List<Person> list(Person person);
}
