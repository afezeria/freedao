package test.java.integration.core.compile.failure.dao;

import io.github.afezeria.freedao.annotation.Dao;
import test.NoProperty;

/**
 * @author afezeria
 */
@Dao(crudEntity = NoProperty.class)
public interface EntityIsObjectBadDao {
}
