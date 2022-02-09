package test;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;

@Dao(crudEntity = Person.class)
public interface JavaDao {

    @XmlTemplate(
            "<select>\n" +
                    "select * from \"user\"\n" +
                    "<where>"+
                    "<if test=\"name != null\">\n" +
                    " and name = #{name}\n" +
                    "</if>\n" +
                    "</where>"+
                    "</select>"
    )
//    @XmlTemplate(
//            "<select>\n" +
//                    "<trim prefix='where'\n" +
//                    "postfixOverrides='and' prefixOverrides='and'>\n" +
//                    "and a = b\n" +
//                    "</trim>\n" +
//                    "</select>\n"
//    )
    List<Person> abc(long id, String name, char a, List<String> list, List l);
}
