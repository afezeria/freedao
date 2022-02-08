package expect;

import com.github.afezeria.freedao.annotation.Dao;

import java.util.List;

/**
 *
 */
@Dao(crudEntity = Person.class)
public interface JavaPersonDao {
    List<Person> selectByName(String name);

//    @XmlTemplate("""
//            <select>
//            select *
//            from person
//            where id in
//            <foreach collection="ids" item="item" separator="," open="(" close=")">
//                #{item}
//            </foreach>
//            </select>
//            """)
//    List<Person> selectByIds(List<Long> ids);

//    @XmlTemplate("""
//            <select>
//            select *
//            from user
//            <where>
//            and name like 'a%'
//            and id in
//                    <foreach collection="list" item="item" separator="," open="(" close=')'>
//                        #{item}
//                    </foreach>
//            </trim>
//            </select>
//            """)
//    List<User> selectByNameLikeAndIdIn(String name, List<Long> ids);
//
//    @XmlTemplate("""
//            <select>
//            select * from user
//            <where>
//            <if test='list.size > 0'>
//            id in
//                    <foreach collection="list" item="item" separator="," open="(" close=')'>
//                        #{item}
//                    </foreach>
//            </if>
//            </trim>
//            </select>
//            """)
//    List<User> select(List<Long> list);
}
