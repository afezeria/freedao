package test.java.integration.core.compile.failure.template.template;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface ExprTypeCastErrorBaoDao {
    @XmlTemplate("""
            <select>
            select * from person where
            id = <if test='a.b>0'>1</if>
            </select>
            """)
    List abc(A a);

    class A {
        private String b;

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }

}
