package test.java.failure.core.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnVoidBadDao {
    void list(Person person);
}