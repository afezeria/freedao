package test.java.integration.core.compile.failure.method.style.crud.count;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ReturnStringCountBadDao {
    String count(Person person);
}
