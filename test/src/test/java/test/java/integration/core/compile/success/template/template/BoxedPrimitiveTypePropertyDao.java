package test.java.integration.core.compile.success.template.template;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface BoxedPrimitiveTypePropertyDao {

    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='a.id == -1.3'>1</if>
            </select>
            """)
    List<Person> query(A a);

    class A {
        private double id;

        public double getId() {
            return id;
        }

        public void setId(double id) {
            this.id = id;
        }
    }
}
