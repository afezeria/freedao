package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Dao;
import test.InvalidResultTypeHandlerEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = InvalidResultTypeHandlerEntity.class)
public interface InvalidResultTypeHandlerBadDao {
}
