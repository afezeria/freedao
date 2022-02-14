package test.java.xml.simple;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
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
