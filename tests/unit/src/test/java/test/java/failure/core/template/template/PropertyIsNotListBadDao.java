package test.java.failure.core.template.template;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface PropertyIsNotListBadDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='id.1>0'>1</if>
            </select>
            """)
    List abc(Long id);

}
