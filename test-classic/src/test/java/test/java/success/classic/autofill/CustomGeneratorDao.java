package test.java.success.classic.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import test.CustomGeneratorEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = CustomGeneratorEntity.class)
public interface CustomGeneratorDao {
    int insert(CustomGeneratorEntity entity);
}
