package test;

import com.github.afezeria.freedao.spring.runtime.DataSourceContextHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.component.DataInitUtil;
import test.component.PersonService;

/**
 * @author afezeria
 */
@SpringBootTest
public class MultipleDataSourceTests {
    @Autowired
    PersonService service;
    @Autowired
    DataInitUtil dataInitUtil;

    @BeforeEach
    public void beforeEach() {
        dataInitUtil.init();
    }

    @Test
    public void multipleDatasourceTest() {
        String[] ints = service.findClassNameAndPersonName(1);
        Assertions.assertArrayEquals(ints, new String[]{"a0", "a0"});
        assert DataSourceContextHolder.get() == null;

    }

}
