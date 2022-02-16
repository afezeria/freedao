package test.java.style.named.cond;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface NotLikeDao {

    @NotNull
    List<Person> queryByNameNotLike(String pattern);

}
