package test.java.failure.core;

import com.github.afezeria.freedao.annotation.Dao;
import test.InvalidParameterTypeHandlerEntity;

/**
 * @author afezeria
 */
@Dao(crudEntity = InvalidParameterTypeHandlerEntity.class)
public interface InvalidParameterTypeHandlerBadDao {
}
