package test.java.failure.core.method.style.named;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface MissingOrderPropertyBadDao {
    List<Person> queryByIdOrderByNonexistentProperty(Long l);
}
