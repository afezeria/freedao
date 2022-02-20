package test.java.style.named.cond;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface LessThanEqualDao {

    @NotNull
    List<Person> queryByIdLessThanEqual(Long i);

}
