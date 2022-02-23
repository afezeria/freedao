package test.java.success.classic;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface BatchInsertDao {
    @XmlTemplate("""
            <insert>
            insert into person (name,active)
            values <foreach collection='list' item='i' separator=','>(#{i.name},#{i.active})</foreach>
            </insert>
            """)
    int batchInsert(List<Person> list);
}
