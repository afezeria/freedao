package test.java.success.core.method.style.named.order;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface OnlyOrderByDao {
    List<Person> queryByOrderByNameDescIdAsc();
}
