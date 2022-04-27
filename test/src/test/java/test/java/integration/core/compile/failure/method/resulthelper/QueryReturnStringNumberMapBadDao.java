package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnStringNumberMapBadDao {
    Map<String, Number> list(Person person);
}
