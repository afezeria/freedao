package test.java.success.core.template.template;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface CastObjectToListDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='list.1 == 0'>1</if>
            </select>
            """)
    List<Person> query(Object list);

}
