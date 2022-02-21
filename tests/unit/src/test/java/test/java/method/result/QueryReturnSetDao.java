package test.java.method.result;

import com.github.afezeria.freedao.annotation.Dao;
import test.PersonOverrideEqual;

import java.util.Set;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonOverrideEqual.class)
public interface QueryReturnSetDao {
    Set<PersonOverrideEqual> all();
}
