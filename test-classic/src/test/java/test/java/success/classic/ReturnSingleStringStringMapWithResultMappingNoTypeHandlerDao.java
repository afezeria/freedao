package test.java.success.classic;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.Mapping;
import com.github.afezeria.freedao.annotation.ResultMappings;
import com.github.afezeria.freedao.annotation.XmlTemplate;

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
