package test;

import com.github.afezeria.freedao.classic.runtime.DaoHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import test.component.DataInitUtil;
import test.component.Db;
import test.component.MultipleDataSourceTestService;
import test.component.OrderDao;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author afezeria
 */
@SpringBootTest
public class MultipleDataSourceTests {
    @Autowired
    MultipleDataSourceTestService service;
    @Autowired
    DataInitUtil dataInitUtil;
    @Autowired
    OrderDao dao;
    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void switchDataSourceByAnnotation() {
        assertEquals(service.findOrderNameById(1), "a1");
        assertEquals(service.getTempTableSize(), 3);
    }

    @Test
    public void switchDataSourceByCode() {
        assert DaoHelper.ds(Db.MASTER_1, () -> {
            return dao.selectOneById(1);
        }) != null;
        assert DaoHelper.ds(Db.MASTER_2, () -> {
            return template.queryForList("select * from " + Db.Table.TEMP_TEST_TABLE).size();
        }) == 3;
    }


}
