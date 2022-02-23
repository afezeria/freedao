package test.java.success.classic;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.PersonAnyId;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface AutoFillWithObjectIdDao {
    @XmlTemplate("""
            <insert>
            insert into person (name)
            values <foreach collection='list' item='i' separator=','>(#{i.name})</foreach>
            </insert>
            """)
    int batchInsert(List<PersonAnyId> list);
}
