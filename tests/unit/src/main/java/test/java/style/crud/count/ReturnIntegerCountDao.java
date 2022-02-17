package test.java.style.crud.count;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 */
@Dao(crudEntity = Person.class)
public interface ReturnIntegerCountDao {

    Integer count();
}
