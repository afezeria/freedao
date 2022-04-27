package test.java.integration.classic.runtime;

import io.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao(crudEntity = DynamicTableNameEntity.class)
public interface DynamicTableNameDao {

    DynamicTableNameEntity selectOneById(Long id);

}
