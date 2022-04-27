package test.java.integration.core.compile.failure.template.template;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface PropertyIsNotMapBadDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='id."abc">0'>1</if>
            </select>
            """)
    List abc(Long id);

}
