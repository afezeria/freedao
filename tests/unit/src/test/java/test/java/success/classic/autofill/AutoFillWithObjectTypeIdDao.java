package test.java.success.classic.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.FillObjectTypeFieldByDbGeneratedKeyEntity;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface AutoFillWithObjectTypeIdDao {
    @XmlTemplate("""
            <insert>
            insert into auto_fill_int_id (name)
            values <foreach collection='list' item='i' separator=','>(#{i.name})</foreach>
            </insert>
            """)
    int batchInsert(List<FillObjectTypeFieldByDbGeneratedKeyEntity> list);
}
