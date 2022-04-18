package test.java.integration.classic.compile.success.autofill;

import com.github.afezeria.freedao.annotation.Dao;
import test.MultiDbGeneratedKeysEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = MultiDbGeneratedKeysEntity.class)
public interface FillMultiGeneratedKeysDao {
    int insert(MultiDbGeneratedKeysEntity entity);
}
