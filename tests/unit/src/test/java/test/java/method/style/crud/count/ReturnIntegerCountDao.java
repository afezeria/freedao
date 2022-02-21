package test.java.method.style.crud.count;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface ReturnIntegerCountDao {

    Integer count();
}
