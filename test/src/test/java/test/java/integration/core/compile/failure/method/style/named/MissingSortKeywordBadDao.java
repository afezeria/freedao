package test.java.integration.core.compile.failure.method.style.named;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface MissingSortKeywordBadDao {
    List<Person> queryByIdOrderById(Long l);
}
