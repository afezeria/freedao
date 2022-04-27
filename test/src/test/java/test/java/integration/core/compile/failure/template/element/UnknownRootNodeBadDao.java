package test.java.integration.core.compile.failure.template.element;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface UnknownRootNodeBadDao {
    @XmlTemplate("""
            <abc>
            </abc>
            """)
    List query();
}
