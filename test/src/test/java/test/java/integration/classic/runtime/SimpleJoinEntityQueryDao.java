package test.java.integration.classic.runtime;

import io.github.afezeria.freedao.annotation.Dao;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = JoinEntityA.class)
public interface SimpleJoinEntityQueryDao {

    List<JoinEntityA> list(JoinEntityA cond);
}
