package test.java.xml.simple;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;
import test.java.named.prefix.PrefixTestEntity;

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
