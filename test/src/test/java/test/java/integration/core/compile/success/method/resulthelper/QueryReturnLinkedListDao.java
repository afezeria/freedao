package test.java.integration.core.compile.success.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.LinkedList;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnLinkedListDao {
    LinkedList<Person> list(Person person);
}
