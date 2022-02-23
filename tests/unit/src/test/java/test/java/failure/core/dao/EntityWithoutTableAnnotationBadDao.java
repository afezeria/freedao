package test.java.failure.core.dao;

import com.github.afezeria.freedao.annotation.Dao;
import test.WithoutTableAnnotation;

/**
 * @author afezeria
 */
@Dao(crudEntity = WithoutTableAnnotation.class)
public interface EntityWithoutTableAnnotationBadDao {
}
