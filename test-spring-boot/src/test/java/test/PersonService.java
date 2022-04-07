package test;

import com.github.afezeria.freedao.spring.runtime.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author afezeria
 */
@Service
public class PersonService {
    @Autowired
    PersonDao dao;
    @Autowired
    JdbcTemplate template;
    @Autowired
    @Lazy
    PersonService self;

    @DS(Db.MASTER_2)
    public String[] findClassNameAndPersonName(Integer id) {
        return new String[]{
                ((String) template.queryForList("select name from clazz where id = ?", id).get(0).get("name")),
                dao.selectOneById(id).getName()
        };
    }
}
