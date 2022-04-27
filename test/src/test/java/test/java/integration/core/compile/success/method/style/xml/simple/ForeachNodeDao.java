package test.java.integration.core.compile.success.method.style.xml.simple;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
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
