package test.java.method.style.named.prefix;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface QueryNotReturnEntityCollectionBadDao {

    List<String> queryByName(@NotNull String name);
}
