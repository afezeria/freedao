package test.java.style.crud.all;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ReturnStringAllDao {
    List<String> all();
}
