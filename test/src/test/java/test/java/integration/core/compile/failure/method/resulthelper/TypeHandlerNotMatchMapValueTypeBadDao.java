package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.Mapping;
import io.github.afezeria.freedao.annotation.ResultMappings;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;
import test.StringResultTypeHandler;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface TypeHandlerNotMatchMapValueTypeBadDao {
    @XmlTemplate("""
            <select>
            select id as uid from person
            </select>
            """)
    @ResultMappings(
            value = {
                    @Mapping(
                            source = "uid",
                            typeHandler = StringResultTypeHandler.class
                    )
            }
    )
    Map<String, Long> all();
}
