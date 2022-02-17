package test.java.style.crud.insert;

import com.github.afezeria.freedao.annotation.Dao;
import org.jetbrains.annotations.NotNull;
import test.Person;

/**
 */
@Dao(crudEntity = Person.class)
public interface ReturnLongPersonInsertDao {

    long insert(@NotNull Person entity);
}
