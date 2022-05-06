package test.java.integration.classic.compile.failure;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

/**
 * @author afezeria
 */
@Dao
public interface QueryMethodReturnCharTypeBadDao {
    @XmlTemplate("""
            <select>
            select * from person
            </select>
            """)
    char query();
}
