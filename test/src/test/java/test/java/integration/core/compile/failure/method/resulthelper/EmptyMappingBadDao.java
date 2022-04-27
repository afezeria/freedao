package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.ResultMappings;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface EmptyMappingBadDao {
    @ResultMappings(value={}, onlyCustomMapping = true)
    List<Person> queryByName(String name);
}
