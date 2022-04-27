package test.java.integration.core.compile.failure;

import io.github.afezeria.freedao.annotation.Dao;
import test.InvalidParameterTypeHandlerEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = InvalidParameterTypeHandlerEntity.class)
public interface InvalidParameterTypeHandlerBadDao {
}
