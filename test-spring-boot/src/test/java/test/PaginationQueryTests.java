package test;

import io.github.afezeria.freedao.classic.runtime.FreedaoGlobalConfiguration;
import io.github.afezeria.freedao.classic.runtime.Page;
import io.github.afezeria.freedao.classic.runtime.context.DaoHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import test.component.DataInitUtil;
import test.component.Order;
import test.component.OrderDao;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author afezeria
 */
@SpringBootTest
public class PaginationQueryTests {
    @Autowired
    OrderDao dao;
    @Autowired
    DataInitUtil dataInitUtil;
    @Autowired
    JdbcTemplate template;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void simple() {
        Page<Order> page = DaoHelper.page(1, 3, () -> dao.list(null));
        assert page.getPageIndex() == 1;
        assert page.getPageSize() == 3;
        Assertions.assertIterableEquals(
                page.getRecords().stream().map(Order::getId).collect(Collectors.toList()),
                List.of(1, 2, 3)
        );
    }

    @Test
    public void offsetGreaterThanCount() {
        Page<Order> page = DaoHelper.page(2, 10, () -> dao.queryByIdGreaterThan(0));
        assert page.getPageIndex() == 2;
        assert page.getPageSize() == 10;
        assert page.getRecords().size() == 0;
        assert page.getTotal() == DataInitUtil.tableSize;
    }

    @Test
    public void pageSizeGreaterThanCount() {
        Page<Order> page = DaoHelper.page(1, 20, () -> dao.list(null));
        assert page.getPageIndex() == 1;
        assert page.getPageSize() == 20;
        assert page.getRecords().size() == DataInitUtil.tableSize;
        assert page.getTotal() == 10;
    }

    @Test
    public void pageSizeGreaterThanMaxPageSize() {
        FreedaoGlobalConfiguration.maxPageSizeLimit = 5;
        try {
            Page<Order> page = DaoHelper.page(1, 20, () -> dao.list(null));
            assert page.getPageIndex() == 1;
            assert page.getPageSize() == 5;
            assert page.getRecords().size() == page.getPageSize();
            assert page.getTotal() == 10;
        } finally {
            FreedaoGlobalConfiguration.maxPageSizeLimit = 10000;
        }
    }

    @Test
    public void pages() {
        Page<Order> page = DaoHelper.page(1, 20, () -> dao.list(null));
        assert page.getPages() == 1;
        page = DaoHelper.page(1, 5, () -> dao.list(null));
        assert page.getPages() == 2;
        page = DaoHelper.page(3, 3, () -> dao.list(null));
        assert page.getPages() == 4;
    }

}
