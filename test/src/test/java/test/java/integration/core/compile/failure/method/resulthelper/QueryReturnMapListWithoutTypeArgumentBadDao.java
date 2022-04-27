package test.java.integration.core.compile.failure.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface QueryReturnMapListWithoutTypeArgumentBadDao {

    @XmlTemplate("""
            <select>
            select * from person
            </select>
            """)
    List<Map> query();
}
