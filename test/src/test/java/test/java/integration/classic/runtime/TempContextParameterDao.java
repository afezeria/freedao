package test.java.integration.classic.runtime;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface TempContextParameterDao {
    @XmlTemplate("""
            <select>
            select * from person where id = #{_context."id"}
            </select>
            """)
    Person select();
}
