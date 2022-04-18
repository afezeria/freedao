package test.java.integration.core.compile.failure;

import com.github.afezeria.freedao.annotation.Dao;
import test.InvalidParameterTypeHandlerEntity;
import test.InvalidResultTypeHandlerEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = InvalidResultTypeHandlerEntity.class)
public interface InvalidResultTypeHandlerBadDao {
}
