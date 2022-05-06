package test.java.integration.classic.compile.failure;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

/**
 * @author afezeria
 */
@Dao
public interface UpdateMethodNotReturnCharBadDao {
    @XmlTemplate("""
            <update>
            select * from person
            </update>
            """)
    char myUpdate();

    @XmlTemplate("""
            <delete>
            select * from person
            </delete>
            """)
    char myDelete();

    @XmlTemplate("""
            <insert>
            select * from person
            </insert>
            """)
    char myInsert();
}
