package test.java.success.classic.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.PersonStringId;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface AutoFillWithTypeHandlerDao {
    @XmlTemplate("""
            <insert>
            insert into person (name)
            values <foreach collection='list' item='i' separator=','>(#{i.name})</foreach>
            </insert>
            """)
    int batchInsert(List<PersonStringId> list);
}
