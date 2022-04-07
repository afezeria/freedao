package test.java.success.classic.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import test.FillByCustomGeneratorWhenUpdateEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = FillByCustomGeneratorWhenUpdateEntity.class)
public interface FillOnUpdateDao {
    int updateNonNullFields(FillByCustomGeneratorWhenUpdateEntity entity);
}
