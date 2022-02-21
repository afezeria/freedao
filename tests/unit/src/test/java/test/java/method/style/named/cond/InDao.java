package test.java.method.style.named.cond;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;
import java.util.Set;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface InDao {

    @NotNull
    List<Person> queryByIdIn(List<Long> id);

    @NotNull
    List<Person> findByIdIn(Set<Long> id);
}
