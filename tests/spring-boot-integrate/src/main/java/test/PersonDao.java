package test;

import com.github.afezeria.freedao.annotation.Dao;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface PersonDao {
    List<Person> queryByIdNotNull();
}
