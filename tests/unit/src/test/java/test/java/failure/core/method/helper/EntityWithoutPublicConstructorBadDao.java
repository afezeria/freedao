package test.java.failure.core.method.helper;

import com.github.afezeria.freedao.annotation.Dao;
import test.PersonWithoutPublicConstructor;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = PersonWithoutPublicConstructor.class)
public interface EntityWithoutPublicConstructorBadDao {
    List<PersonWithoutPublicConstructor> all();
}