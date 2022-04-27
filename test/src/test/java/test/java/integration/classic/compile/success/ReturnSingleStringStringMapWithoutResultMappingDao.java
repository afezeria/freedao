package test.java.integration.classic.compile.success;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface ReturnSingleStringStringMapWithoutResultMappingDao {
    @XmlTemplate("""
            <select>
            select name from person where id = #{id}
            </select>
            """)
    Map<String, String> query(Long id);
}
