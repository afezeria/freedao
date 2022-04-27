package test.java.integration.core.compile.failure.template.expr;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface ParseFailureExprBadDao {
    @XmlTemplate("""
            <select>
            <if test='a'></if></select>
            """)
    List<Person> query();
}
