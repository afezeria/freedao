package test.java.integration.core.compile.success.method.style.xml.simple;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface TrimNodeDao {
    @XmlTemplate("""
            <select>
              select * from person where id in (<trim prefixOverrides='' suffixOverrides=','>#{id1},#{id2},</trim>)
            </select>
                        """)
    List<Person> query(Long id1, Long id2);
}
