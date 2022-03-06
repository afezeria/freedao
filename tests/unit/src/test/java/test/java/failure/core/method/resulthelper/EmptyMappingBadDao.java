package test.java.failure.core.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.ResultMappings;
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
