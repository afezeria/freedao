package test.java.integration.classic.compile.success;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.Mapping;
import io.github.afezeria.freedao.annotation.ResultMappings;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.PersonWithConstructor;

/**
 * @author afezeria
 */
@Dao
public interface QueryEntityWithCustomMappingDao {
    @XmlTemplate("""
            <select>
            select * from person where id = #{id}
            </select>
            """)
    @ResultMappings({
            @Mapping(
                    source = "nick_name",
                    target = "name"
            )
    })
    PersonWithConstructor query(Long id);
}
