package test.java.failure.core.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnNumberBadDao {
    Number list(Person person);
}
