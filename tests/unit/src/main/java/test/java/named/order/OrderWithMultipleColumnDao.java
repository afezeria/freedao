package test.java.named.order;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface OrderWithMultipleColumnDao {

//    List<Person> queryByNameOrderByNameAscIdAsc(String name);
//
//    List<Person> queryByNameOrderByNameAscIdDesc(String name);
//
//    List<Person> queryByNameOrderByNameDescIdAsc(String name);

    @NotNull
    List<Person> queryByWhenCreatedIsNotNullOrderByNameAscIdAsc(@NotNull String s);
}
