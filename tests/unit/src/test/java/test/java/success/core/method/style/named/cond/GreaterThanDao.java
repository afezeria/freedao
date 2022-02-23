package test.java.success.core.method.style.named.cond;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface GreaterThanDao {

    @NotNull
    List<Person> queryByIdGreaterThan(Long i);
}
