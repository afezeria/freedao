package test.java.success.core.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.Mapping;
import com.github.afezeria.freedao.annotation.ResultMappings;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.PersonWithRequiredId;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface QueryWithOverrideParameterMappingDao {
    @XmlTemplate("""
            <select>
            select id as uid from person
            </select>
            """)
    @ResultMappings(
            value = {
                    @Mapping(
                            source = "uid",
                            target = "id"
                    )
            },
            onlyCustomMapping = true
    )
    List<PersonWithRequiredId> query();
}
