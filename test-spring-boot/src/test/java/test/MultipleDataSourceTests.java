package test;

import io.github.afezeria.freedao.classic.runtime.SqlHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import test.component.*;

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

    @Autowired
    Master1OrderDao master1OrderDao;
    @Autowired
    Master2OrderDao master2OrderDao;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void switchDataSourceByAnnotation() {
        //master1
        assertEquals(service.findOrderNameById(1), "a1");
        //master2
        assertEquals(service.getTempTableSize(), 3);
    }

    @Test
    public void switchDataSourceByCode() {
        assert SqlHelper.ds(Db.MASTER_1, () -> {
            return dao.selectOneById(1);
        }) != null;
        assert SqlHelper.ds(Db.MASTER_2, () -> {
            return template.queryForList("select * from " + Db.Table.TEMP_TEST_TABLE).size();
        }) == 3;
    }

    @Test
    public void switchDsByAnnotationOnClass() {
        User master1User = master1OrderDao.selectOneById(1L);
        User master2User = master2OrderDao.selectOneById(1L);
        assertEquals(master1User.getName(), "master1");
        assertEquals(master2User.getName(), "master2");
    }

}
