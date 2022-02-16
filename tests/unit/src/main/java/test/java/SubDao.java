package test.java;

import com.github.afezeria.freedao.annotation.Dao;
import test.Person;

import java.util.List;
import java.util.Map;

@Dao(crudEntity = Person.class)
public interface SubDao extends BaseDao<Person> {

}