package test.java.success.classic.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import test.CustomIdGeneratorEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = CustomIdGeneratorEntity.class)
public interface CustomGeneratorWithBatchInsertDao {
    int insert(CustomIdGeneratorEntity entity);
}
