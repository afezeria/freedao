package test.java.integration.core.compile.failure.template.element;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface ErrorParameterTypeHandlerNameBadDao {
    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=a b c}
            </select>
            """)
    List<Person> select(String name);
}
