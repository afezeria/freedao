package test.java.template.element;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface MergeTextNodeDao {
    @XmlTemplate("""
            <select>
            select id
            <![CDATA[
            ,'${test}' as name
            ]]>
            from person
            </select>
            """)
    List<Map<String, Object>> query();
}
