package test.java.failure.core.method.style.named;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface DeleteMethodContainSortBadDao {
    int deleteByOrderByIdAsc();
}
