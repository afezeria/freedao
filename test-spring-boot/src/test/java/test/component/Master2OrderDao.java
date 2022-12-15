package test.component;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.classic.runtime.DS;

/**
 * @author afezeria
 */
@Dao(crudEntity = User.class)
@DS(Db.MASTER_2)
public interface Master2OrderDao {
    User selectOneById(Long id);
}
