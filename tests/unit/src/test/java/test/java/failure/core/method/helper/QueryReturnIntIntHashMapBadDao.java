package test.java.failure.core.method.helper;

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
