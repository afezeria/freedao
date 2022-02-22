package test.java.classic;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.Mapping;
import com.github.afezeria.freedao.annotation.ResultMappings;
import com.github.afezeria.freedao.annotation.XmlTemplate;
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
