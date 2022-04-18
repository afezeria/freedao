package test.java.integration.classic.compile.success;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.PersonWithConstructor;

/**
 * @author afezeria
 */
@Dao
public interface ReturnEntityWithConstructorDao {
    @XmlTemplate("""
            <select>
            select * from person where id = #{id}
            </select>
            """)
    PersonWithConstructor query(Long id);
}
