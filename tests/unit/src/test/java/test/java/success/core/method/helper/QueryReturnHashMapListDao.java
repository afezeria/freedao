package test.java.success.core.method.helper;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * @author afezeria
 */
@Dao
public interface QueryReturnHashMapListDao {
    @XmlTemplate("""
            <select>
            select * from person
            </select>
            """)
    List<HashMap<String, Object>> query();
}
