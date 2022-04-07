package test.java.failure.core.template.element;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

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
