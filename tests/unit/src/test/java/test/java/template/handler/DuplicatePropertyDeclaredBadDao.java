package test.java.template.handler;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface DuplicatePropertyDeclaredBadDao {
    @XmlTemplate("""
            <select>
            select * from person where 
            id in (<foreach collection='ids' item='i'>#{i}</foreach>)
            and name in (<foreach collection='names' item='i'>#{i}</foreach>)
            </select>
            """)
    List abc(List<Long> ids, List<String> names);
}
