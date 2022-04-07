package test.java.success.core.method.style.xml;

import com.github.afezeria.freedao.annotation.Dao;
import com.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface EmptyTemplateDao {
    @XmlTemplate("")
    Map<String, Object> find();
}
