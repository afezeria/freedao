package test.java.integration.core.compile.success.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.Mapping;
import io.github.afezeria.freedao.annotation.ResultMappings;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface QueryReturnSingleColumnDao {
    @XmlTemplate("""
            <select>
            select id as uid from person
            </select>
            """)
    @ResultMappings(
            value = {
                    @Mapping(
                            source = "uid"
                    )
            }
    )
    List<Long> query();
}
