package test.java.integration.core.compile.success;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

@Dao(crudEntity = Person.class)
public interface SubAllDao extends BaseAllDao<Person> {

}