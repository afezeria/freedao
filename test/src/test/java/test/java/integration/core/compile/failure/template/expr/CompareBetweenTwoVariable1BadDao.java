package test.java.integration.core.compile.failure.template.expr;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface CompareBetweenTwoVariable1BadDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='a > b'>1</if>
            </select>
            """)
    List<Person> query(List a,List b);

}
