package test.java.integration.core.compile.success.method.style.xml.simple;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface WhereNodeDao {
    @XmlTemplate("""
            <select>
              select * from person
              <where>
              <if test='id != null'> and id = #{id}</if>
              </where>
            </select>
                        """)
    List<Person> query(Long id);
}
