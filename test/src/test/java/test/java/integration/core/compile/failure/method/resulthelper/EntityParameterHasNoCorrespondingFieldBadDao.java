package test.java.integration.core.compile.failure.method.resulthelper;

import com.github.afezeria.freedao.annotation.Dao;
import test.PersonBad1;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonBad1.class)
public interface EntityParameterHasNoCorrespondingFieldBadDao {
    List<PersonBad1> list(PersonBad1 entity);
}
