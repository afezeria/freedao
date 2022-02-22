package test.java.classic;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.Mapping;
import com.github.afezeria.freedao.annotation.ResultMappings;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface ReturnSingleMapWithResultMappingNoTypeHandlerDao {
    @XmlTemplate("""
            <select>
            select * from person where id = #{id}
            </select>
            """)
    @ResultMappings({
            @Mapping(source = "id")
    })
    Map<String, Object> query(Long id);
}
