package test.java.success.core.template.template;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface CompareMapSizeDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='map.size == 0'>1</if>
            </select>
            """)
    List<Person> query(Map map);

}
