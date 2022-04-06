package test.java.success.classic.contextparameter;

import com.github.afezeria.freedao.annotation.Dao;

/**
 * @author afezeria
 */
@Dao(crudEntity = DynamicTableNameEntity.class)
public interface DynamicTableNameDao {

    DynamicTableNameEntity selectOneById(Long id);

}
