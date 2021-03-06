package test.java.integration.core.compile.success.method.style.named.order;

import io.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface OnlyOrderByDao {
    List<Person> queryByOrderByNameDescIdAsc();
}
