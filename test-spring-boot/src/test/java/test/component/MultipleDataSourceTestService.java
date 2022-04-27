package test.component;

import io.github.afezeria.freedao.classic.runtime.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author afezeria
 */
@Service
public class MultipleDataSourceTestService {
    @Autowired
    OrderDao dao;
    @Autowired
    JdbcTemplate template;
    @Autowired
    @Lazy
    MultipleDataSourceTestService self;

    @DS(Db.MASTER_1)
    public String findOrderNameById(Integer id) {
        return dao.selectOneById(id).getName();
    }

    @DS(Db.MASTER_2)
    public Long getTempTableSize() {
        return (Long) template.queryForMap("select count(*) as cot from " + Db.Table.TEMP_TEST_TABLE).get("cot");
    }
}
