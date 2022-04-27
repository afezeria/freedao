package test.java.integration.classic.compile.success.autofill;

import io.github.afezeria.freedao.annotation.Dao;
import test.CustomGeneratorEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = CustomGeneratorEntity.class)
public interface CustomGeneratorDao {
    int insert(CustomGeneratorEntity entity);
}
