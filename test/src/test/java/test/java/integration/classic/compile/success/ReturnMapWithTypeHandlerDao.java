package test.java.integration.classic.compile.success;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.Mapping;
import io.github.afezeria.freedao.annotation.ResultMappings;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.CharacterResultTypeHandler;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface ReturnMapWithTypeHandlerDao {
    @XmlTemplate("""
            <select>
            select * from person
            </select>
            """)
    @ResultMappings({
            @Mapping(source = "id"),
            @Mapping(source = "name", typeHandler = CharacterResultTypeHandler.class),
    })
    List<Map<String, Object>> all();

}
