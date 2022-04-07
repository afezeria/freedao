package test;

import com.github.afezeria.freedao.classic.runtime.context.DaoContext;
import com.github.afezeria.freedao.spring.runtime.DataSourceContextHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

/**
 *
 */
@SpringBootTest
public class SpringBootIntegrateApplicationTests {
    @Autowired
    PersonDao dao;
    @Autowired
    DaoContext daoContext;
    @Autowired
    JdbcTemplate template;
    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    PersonService service;

    @Test
    public void multipleDatasourceTest() {
        String[] ints = service.findClassNameAndPersonName(1);
        Assertions.assertArrayEquals(ints, new String[]{"a", "a"});
        assert DataSourceContextHolder.get() == null;

    }

    @Test
    public void springTxIntegration() {
        int personCount = dao.count(null);
        try {
            transactionTemplate.executeWithoutResult(status -> {
                Person person = new Person();
                person.setName("test");
                person.setActive(true);
                assert dao.insert(person) == 1;

                int cot = Objects.requireNonNull(template.queryForObject("select count(*) from person", Integer.class));
                assert cot == personCount + 1;

                throw new RuntimeException();
            });
        } catch (RuntimeException ignored) {
        }
        assert dao.count(null) == personCount;

        transactionTemplate.executeWithoutResult(status -> {
            Person person = new Person();
            person.setName("test2");
            person.setActive(true);
            assert dao.insert(person) == 1;

            int cot = Objects.requireNonNull(template.queryForObject("select count(*) from person", Integer.class));
            assert cot == personCount + 1;
        });
        assert dao.count(null) == personCount + 1;
    }

    @Test
    public void getConnection() {
        Object[] arr = new Object[2];
        daoContext.withTx(conn -> {
            arr[0] = conn;
            return null;
        });
        daoContext.withTx(conn -> {
            arr[1] = conn;
            return null;
        });
        assert arr[0] != arr[1];
    }
}
