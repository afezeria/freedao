package test.java.integration.classic.compile.success.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import test.MultipleFieldCustomGeneratorEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = MultipleFieldCustomGeneratorEntity.class)
public interface MultipleFieldCustomGeneratorDao {
    int insert(MultipleFieldCustomGeneratorEntity entity);
}
