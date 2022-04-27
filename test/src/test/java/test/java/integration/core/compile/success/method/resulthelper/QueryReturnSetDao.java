package test.java.integration.core.compile.success.method.resulthelper;

import io.github.afezeria.freedao.annotation.Dao;
import test.PersonOverrideEqual;

import java.util.Set;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonOverrideEqual.class)
public interface QueryReturnSetDao {
    Set<PersonOverrideEqual> list(PersonOverrideEqual personOverrideEqual);
}
