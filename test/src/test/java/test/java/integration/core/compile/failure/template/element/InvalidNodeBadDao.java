package test.java.integration.core.compile.failure.template.element;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface InvalidNodeBadDao {
    @XmlTemplate("""
            <select>
            <abc></abc>
            </select>
            """)
    List query();
}
