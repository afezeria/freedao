package test.java.method.style.xml.simple;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface TrimNodeDao {
    @XmlTemplate("""
            <select>
              select * from person where id in (<trim prefixOverrides='' postfixOverrides=','>#{id1},#{id2},</trim>)
            </select>
                        """)
    List<Person> query(Long id1, Long id2);
}
