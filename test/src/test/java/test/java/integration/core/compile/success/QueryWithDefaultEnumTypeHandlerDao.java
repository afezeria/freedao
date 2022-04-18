package test.java.integration.core.compile.success;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;
import test.PersonType;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryWithDefaultEnumTypeHandlerDao {
    List<Person> selectBySearchType(PersonType searchType);
}
