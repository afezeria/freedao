package test.java.integration.core.compile.failure.method;

import io.github.afezeria.freedao.annotation.Dao;
import io.github.afezeria.freedao.annotation.XmlTemplate;

/**
 * @author afezeria
 */
@Dao
public interface InvalidParameterNameBadDao {
    @XmlTemplate("")
    int abc(String _test);
}
