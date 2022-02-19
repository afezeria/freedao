package test.java.style.named;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryByIdAndPassRuntimeContext {
    List<Person> queryById(Map<String, Object> context, Long id);
}
