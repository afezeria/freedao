package test.java.failure.core.template.template;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface MissingPropertyBadDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='person.company != null'>1</if>
            </select>
            """)
    List abc(Person person);

}
