package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnListListBadDao {
    List<ArrayList<Person>> list(Person person);
}
