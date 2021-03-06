package test.java.integration.core.compile.success.method.style.xml.simple;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao()
public interface IfNodeDao {

    @XmlTemplate("""
            <select>
            select * from person where 1=1 <if test='name != null'>and name = #{name}</if>
            </select>
            """)
    List<Person> queryByNameIfNameNotNull(String name);

}
