package test.java.style.crud.count;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.math.BigInteger;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface ReturnStringCountBadDao {
    String count();
}