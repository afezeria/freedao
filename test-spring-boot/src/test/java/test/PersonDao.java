package test;

import com.github.afezeria.freedao.annotation.Dao;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface PersonDao {
    List<Person> queryByIdNotNull();

    int insert(Person person);

    Integer count(Person person);
}
