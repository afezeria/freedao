package test.java.integration.classic.compile.success.autofill;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.CustomGeneratorEntity;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface CustomGeneratorBatchInsertDao {
    @XmlTemplate("""
            <insert>
            insert into auto_fill_int_id (name)
            values <foreach collection='list' item='i' separator=','>(#{i.name})</foreach>
            </insert>
            """)
    int batchInsert(List<CustomGeneratorEntity> list);
}
