package test;

import com.github.afezeria.freedao.classic.runtime.DaoHelper;
import com.github.afezeria.freedao.classic.runtime.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import test.component.DataInitUtil;
import test.component.Person;
import test.component.PersonDao;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author afezeria
 */
@SpringBootTest
public class PaginationQueryTests {
    @Autowired
    PersonDao dao;
    @Autowired
    DataInitUtil dataInitUtil;
    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void test1() {
        Page<Person> page = DaoHelper.pagination(1, 5, () -> dao.queryByIdNotNull());
        assert page.getPageIndex() == 1;
        assert page.getPageSize() == 5;
        Assertions.assertIterableEquals(
                page.getRecords().stream().map(Person::getId).collect(Collectors.toList()),
                List.of(1, 2, 3, 4, 5)
        );
    }

    @Test
    public void test2() {
        Page<Person> page = DaoHelper.pagination(5, 5, () -> dao.queryByIdNotNull());
        assert page.getPageIndex() == 5;
        assert page.getPageSize() == 5;
        assert page.getRecords().size() == 0;
        assert page.getCount() == 20;
    }

    @Test
    public void test3() {
        Page<Person> page = DaoHelper.pagination(5, 5, () -> dao.queryByIdNotNull());
        assert page.getPageIndex() == 5;
        assert page.getPageSize() == 5;
        assert page.getRecords().size() == 0;
        assert page.getCount() == 20;
    }
}
