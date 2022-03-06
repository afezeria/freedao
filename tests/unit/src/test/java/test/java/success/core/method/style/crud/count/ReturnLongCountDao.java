package test.java.success.core.method.style.crud.count;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ReturnLongCountDao {
    Long count(Person person);
}
