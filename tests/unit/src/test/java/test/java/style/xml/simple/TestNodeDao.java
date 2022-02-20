package test.java.style.xml.simple;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface TestNodeDao {
    @XmlTemplate("""
            <select>
            select * from ${table} where name = #{name}
            </select>
            """)
    List<Map<String, Object>> query(String table, String name);
}
