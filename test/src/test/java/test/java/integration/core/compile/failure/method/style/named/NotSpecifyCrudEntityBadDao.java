package test.java.integration.core.compile.failure.method.style.named;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface NotSpecifyCrudEntityBadDao {
    List<Person> queryById();
}
