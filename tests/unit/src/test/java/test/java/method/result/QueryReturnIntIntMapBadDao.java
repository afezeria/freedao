package test.java.method.result;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnIntIntMapBadDao {
    Map<Integer,Integer> all();
}
