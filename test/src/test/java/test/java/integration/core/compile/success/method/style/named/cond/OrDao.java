package test.java.integration.core.compile.success.method.style.named.cond;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface OrDao {
    List<Person> queryByIdOrName(Long id, String name);
}
