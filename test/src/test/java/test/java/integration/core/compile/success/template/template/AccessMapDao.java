package test.java.integration.core.compile.success.template.template;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface AccessMapDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='map."a" == 0'>1</if>
            </select>
            """)
    List<Person> query(Map map);

}
