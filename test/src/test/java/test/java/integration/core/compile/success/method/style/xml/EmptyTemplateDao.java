package test.java.integration.core.compile.success.method.style.xml;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

import java.util.Map;

/**
 * @author afezeria
 */
@Dao
public interface EmptyTemplateDao {
    @XmlTemplate("")
    Map<String, Object> find();
}
