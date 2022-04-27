package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.HashMap;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnIntIntHashMapBadDao {
    HashMap<Integer, Integer> list(Person person);
}
