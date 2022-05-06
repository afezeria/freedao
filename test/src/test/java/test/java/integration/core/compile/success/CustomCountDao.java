package test.java.integration.core.compile.success;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

/**
 * @author afezeria
 */
@Dao
public interface CustomCountDao {
    @XmlTemplate("""
            <select>
            select count(*) from person
            </select>
            """)
    int count();
}
