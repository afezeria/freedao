package test.java.dao;

import com.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao(crudEntity = EntityWithoutTableAnnotation.class)
public interface EntityWithoutTableAnnotationBadDao {
}
