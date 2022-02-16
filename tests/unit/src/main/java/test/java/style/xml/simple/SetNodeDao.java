package test.java.style.xml.simple;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.time.LocalDateTime;

/**
 * @author afezeria
 */
@Dao
public interface SetNodeDao {
    @XmlTemplate("""
            <update>
            update person
            <set>
            <if test='name != null'>name = #{name},</if>
            <if test='active != null'>active = #{active},</if>
            <if test='whenCreated != null'>when_created = #{whenCreated},</if>
            </set>
            where id = #{id}
            </update>
            """)
    int update(Long id, String name, Boolean active, LocalDateTime whenCreated);
}
