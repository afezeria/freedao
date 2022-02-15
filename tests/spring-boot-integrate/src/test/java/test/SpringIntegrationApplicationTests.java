package test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 */
@SpringBootTest
public class SpringIntegrationApplicationTests {
    @Autowired
    JavaDao dao;

    @Test
    public void contextLoads() {
        System.out.println(dao.abc(1L, "y", 'c', null, null));
    }
}
