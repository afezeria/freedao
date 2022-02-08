package test.java.crud.count;

import com.github.afezeria.freedao.annotation.Dao;

/**
 */
@Dao(crudEntity = PersonCount.class)
public interface PersonCountDao {

    Integer count();
}
