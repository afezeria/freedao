package test.java.failure.core.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.HashMap;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnStringNumberHashMapBadDao {
    HashMap<String, Number> list(Person person);
}
