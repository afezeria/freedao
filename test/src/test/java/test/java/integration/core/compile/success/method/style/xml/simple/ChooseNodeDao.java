package test.java.integration.core.compile.success.method.style.xml.simple;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface ChooseNodeDao {
    @XmlTemplate("""
            <select>
              select * from person where 1=1
              <choose>
                <when test="id != null">
                    and id = #{id}
                </when>
                <when test="name != null">
                    and name = #{name}
                </when>
                <otherwise>
                    and active = true
                </otherwise>
              </choose>
            </select>
                        """)
    List<Person> query(Long id, String name);
}
