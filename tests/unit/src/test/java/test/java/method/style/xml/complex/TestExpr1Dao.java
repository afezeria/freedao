package test.java.method.style.xml.complex;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface TestExpr1Dao {
    @XmlTemplate("""
            <select>
            select * from person <if test='(a.g == b ) and c > 1L and (b and d and e &lt;= -1.2) and (f == null) and g or true and (h.0."abc".c.2 > 1)'>where id=2</if>
            </select>
            """)
    List<Person> query(A a,
                       Boolean b,
                       Long c,
                       Boolean d,
                       Double e,
                       Object f,
                       Boolean g,
                       List<Map<String, D>> h);

    class A {
        Boolean g;

        public Boolean getG() {
            return g;
        }

        public A setG(Boolean g) {
            this.g = g;
            return this;
        }
    }

    class D {
        List<Integer> c;

        public List<Integer> getC() {
            return c;
        }

        public D setC(List<Integer> c) {
            this.c = c;
            return this;
        }
    }
}
