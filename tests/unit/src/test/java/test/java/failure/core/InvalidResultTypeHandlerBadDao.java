package test.java.failure.core;

import com.github.afezeria.freedao.annotation.Dao;
import test.InvalidParameterTypeHandlerEntity;
import test.InvalidResultTypeHandlerEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = InvalidResultTypeHandlerEntity.class)
public interface InvalidResultTypeHandlerBadDao {
}
