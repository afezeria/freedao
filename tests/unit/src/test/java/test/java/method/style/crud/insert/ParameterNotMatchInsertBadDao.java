package test.java.method.style.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ParameterNotMatchInsertBadDao {
    int insert(Map<String, Object> map);
}
