package test.java.integration.core.compile.success.method.style.xml.simple;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface JavaNodeDao {
    @XmlTemplate("""
            <select>
              select * from person where name like <java>${namePrefix}+"%"</java>
            </select>
                        """)
    List<Person> query(String namePrefix);
}
