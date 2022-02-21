package test.java.method.result;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.HashMap;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnIntIntHashMapBadDao {
    HashMap<Integer,Integer> all();
}
