package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.HashMap;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnStringNumberHashMapBadDao {
    HashMap<String, Number> list(Person person);
}
