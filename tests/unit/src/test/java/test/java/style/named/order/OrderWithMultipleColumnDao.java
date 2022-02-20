package test.java.style.named.order;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface OrderWithMultipleColumnDao {

    @NotNull
    List<Person> queryByWhenCreatedNotNullOrderByNameAscIdAsc();

    @NotNull
    List<Person> queryByIdNotNullOrderByNameAscIdDesc();
}
