package test.java.integration.core.compile.success.template.element;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface SqlArgumentWithTypeHandler1Dao {
    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=test.Enum2StringParameterTypeHandler}
            </select>
            """)
    List<Person> select(PersonNameEnum name);
}
