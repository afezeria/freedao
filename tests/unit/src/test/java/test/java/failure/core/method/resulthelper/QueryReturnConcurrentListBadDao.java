package test.java.failure.core.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.kotlin.com.intellij.util.containers.ConcurrentList;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnConcurrentListBadDao {
    ConcurrentList<Person> list(Person person);
}
