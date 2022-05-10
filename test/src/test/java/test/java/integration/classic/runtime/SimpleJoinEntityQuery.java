package test.java.integration.classic.runtime;

import io.github.afezeria.freedao.annotation.Dao;
import test.JoinEntityA;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = JoinEntityA.class)
public interface SimpleJoinEntityQuery {

    List<JoinEntityA> list(JoinEntityA cond);
}
