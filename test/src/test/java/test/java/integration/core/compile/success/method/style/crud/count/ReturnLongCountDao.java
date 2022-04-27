package test.java.integration.core.compile.success.method.style.crud.count;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ReturnLongCountDao {
    Long count(Person person);
}
