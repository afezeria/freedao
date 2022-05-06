package test.java.integration.classic.runtime;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

/**
 * @author afezeria
 */
@Dao
public interface NoRowReturnedDao {
    @XmlTemplate("""
            <select>
            select * from person
            </select>
            """)
    int query(Long id);

}
