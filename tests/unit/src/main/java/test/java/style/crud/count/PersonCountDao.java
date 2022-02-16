package test.java.style.crud.count;

import com.github.afezeria.freedao.annotation.Dao;

/**
 */
@Dao(crudEntity = PersonCount.class)
public interface PersonCountDao {

    Integer count();
}
