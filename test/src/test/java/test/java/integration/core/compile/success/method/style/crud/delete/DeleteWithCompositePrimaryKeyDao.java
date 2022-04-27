package test.java.integration.core.compile.success.method.style.crud.delete;

import io.github.afezeria.freedao.annotation.Dao;
import test.Clazz;

/**
 * @author afezeria
 */
@Dao(crudEntity = Clazz.class)
public interface DeleteWithCompositePrimaryKeyDao {
    int delete(Clazz entity);
}
