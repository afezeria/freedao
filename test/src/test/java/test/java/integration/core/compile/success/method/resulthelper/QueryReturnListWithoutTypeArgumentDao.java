package test.java.integration.core.compile.success.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface QueryReturnListWithoutTypeArgumentDao {
    @XmlTemplate("""
            <select>
            select id from person
            </select>
            """)
    List query();
}
