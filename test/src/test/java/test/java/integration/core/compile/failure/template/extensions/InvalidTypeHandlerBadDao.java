package test.java.integration.core.compile.failure.template.extensions;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;
import test.Person;

import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface InvalidTypeHandlerBadDao {
    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler1}
            </select>
            """)
    List<Person> select1(String name);

    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler2}
            </select>
            """)
    List<Person> select2(String name);

    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler3}
            </select>
            """)
    List<Person> select3(String name);

    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=test.java.integration.core.compile.failure.template.extensions.InvalidParameterTypeHandler4}
            </select>
            """)
    List<Person> select4(String name);

    /**
     * typeHandler的handle方法的参数类型和name变量不匹配
     *
     * @param name
     * @return
     */
    @XmlTemplate("""
            <select>
            select * from person where name = #{name,typeHandler=test.Enum2StringParameterTypeHandler}
            </select>
            """)
    List<Person> select5(String name);
}
