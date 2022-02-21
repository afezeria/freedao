package test.java.method.result;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.LinkedList;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnLinkedListDao {
    LinkedList<Person> all();
}
