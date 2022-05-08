package test;

import io.github.afezeria.freedao.classic.runtime.DaoUtil;
import io.github.afezeria.freedao.classic.runtime.context.DaoContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.component.DataInitUtil;
import test.component.OrderDao;

/**
 * @author afezeria
 */
@SpringBootTest
public class DaoUtilTests {
    @Autowired
    DataInitUtil dataInitUtil;
    @Autowired
    DaoContext context;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void simple() {
        OrderDao instance = DaoUtil.getInstance(OrderDao.class, context);
        Integer count = instance.count(null);
        assert count == 10;
    }
}
