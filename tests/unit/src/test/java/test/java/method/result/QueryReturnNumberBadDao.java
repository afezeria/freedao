package test.java.method.result;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnNumberBadDao {
    Number all();
}
