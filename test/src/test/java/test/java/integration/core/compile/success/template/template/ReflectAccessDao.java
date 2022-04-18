package test.java.integration.core.compile.success.template.template;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface ReflectAccessDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='a.a == "b"'>1</if>
            </select>
            """)
    List<Person> query(Object a);

    class A{
        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }
}
