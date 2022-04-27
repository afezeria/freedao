package test.java.integration.classic.compile.success;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.Mapping;
import io.github.afezeria.freedao.annotation.ResultMappings;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface ReturnSingleStringStringMapWithResultMappingNoTypeHandlerDao {
    @XmlTemplate("""
            <select>
            select * from person where id = #{id}
            </select>
            """)
    @ResultMappings({
            @Mapping(source = "name")
    })
    Map<String, String> query(Long id);
}
