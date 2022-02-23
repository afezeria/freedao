package test.java.failure.core.method.helper;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnListListBadDao {
    List<ArrayList<Person>> all();
}
