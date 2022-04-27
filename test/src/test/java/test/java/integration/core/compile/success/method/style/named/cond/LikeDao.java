package test.java.integration.core.compile.success.method.style.named.cond;

import io.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface LikeDao {

    @NotNull
    List<Person> queryByNameLike(String pattern);
}
