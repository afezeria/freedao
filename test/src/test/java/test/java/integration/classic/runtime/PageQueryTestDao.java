package test.java.integration.classic.runtime;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao(crudEntity = Person.class)
public interface PageQueryTestDao {
    List<Person> list(Person entity);

    List<Person> selectByOrderByIdAsc();

    List<Person> selectByIdGreaterThan(Long id);

    @XmlTemplate("""
            <select>
            select * from person
            <where>
            <if test='idGreaterThan != null'>
            id > #{idGreaterThan}
            </if>
            </where>
            <if test='orderBy != null'>
            order by ${orderBy}
            </if>
            <if test='limit != null'>
            limit #{limit}
            </if>
            <if test='offset != null'>
            offset #{offset}
            </if>
            </select>
            """)
    List<Person> selectByXml(Long idGreaterThan, String orderBy, Integer limit, Integer offset);

    int insert(Person entity);

    @XmlTemplate("""
            <select>
            ${sql}
            </select>
            """)
    List<Person> selectBySql(String sql);

    Person selectOneById(Long id);
}
