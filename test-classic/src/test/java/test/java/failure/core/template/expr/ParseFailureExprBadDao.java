package test.java.failure.core.template.expr;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
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