package test.java.integration.core.compile.success.method.style.crud.count;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface ReturnIntegerCountDao {

    Integer count(Person person);
}
