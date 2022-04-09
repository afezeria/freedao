package test;

import com.github.afezeria.freedao.classic.runtime.context.DaoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import test.component.*;

import java.util.Objects;

/**
 *
 */
@SpringBootTest
public class TransactionTest {
    @Autowired
    OrderDao dao;
    @Autowired
    DaoContext context;
    @Autowired
    JdbcTemplate template;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    MultipleDataSourceTestService service;
    @Autowired
    DataInitUtil dataInitUtil;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void springTxIntegration() {
        int daoCount = dao.count(null);
        transactionTemplate.executeWithoutResult(status -> {
            Order person = new Order();
            person.setName("test");
            assert dao.insert(person) == 1;

            int cot = Objects.requireNonNull(template.queryForObject("select count(*) from " + Db.Table.ORDER, Integer.class));
            assert cot == daoCount + 1;
            status.setRollbackOnly();
        });
        assert dao.count(null) == daoCount;

        transactionTemplate.executeWithoutResult(status -> {
            Order person = new Order();
            person.setName("test2");
            assert dao.insert(person) == 1;

            int cot = Objects.requireNonNull(template.queryForObject("select count(*) from " + Db.Table.ORDER, Integer.class));
            assert cot == daoCount + 1;
        });
        assert dao.count(null) == daoCount + 1;
    }

    @Test
    public void getConnection() {
        Object[] arr = new Object[2];
        context.withConnection(conn -> {
            arr[0] = conn;
            return null;
        });
        context.withConnection(conn -> {
            arr[1] = conn;
            return null;
        });
        assert arr[0] != arr[1];
    }
}
