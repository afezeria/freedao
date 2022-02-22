package test.java.method.result;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.Mapping;
import com.github.afezeria.freedao.annotation.ResultMappings;
import test.Person;
import test.StringResultTypeHandler;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface TypeHandlerNotMatchFieldBadDao {
    @ResultMappings(
            value = {
                    @Mapping(
                            source="",
                            target="id",
                            typeHandler = StringResultTypeHandler.class
                    )
            }
    )
    List<Person> all();
}
