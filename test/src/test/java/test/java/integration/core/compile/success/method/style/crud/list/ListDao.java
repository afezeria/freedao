package test.java.integration.core.compile.success.method.style.crud.list;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ListDao {
    List<Person> list(Person person);
}
