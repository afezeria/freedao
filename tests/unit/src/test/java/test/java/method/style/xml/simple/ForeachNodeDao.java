package test.java.method.style.xml.simple;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
@Repository
public interface ForeachNodeDao {
    @NotNull
    @XmlTemplate("""
            <select>
            select * from person
            where id in
            <foreach collection='list' item='i' separator=',' open='(' close=')'>
            #{i}
            </foreach>
            </select>
            """)
    List<Person> queryIdIn(List<Long> list);
}
