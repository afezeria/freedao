package test.java.style.named;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface MissingParameterBaoDao {
    List<Person> queryById();
}
