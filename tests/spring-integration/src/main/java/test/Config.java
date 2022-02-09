package test;

import com.github.afezeria.freedao.runtime.classic.DaoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.LinkedHashMap;

/**
 */
@Configuration
public class Config {
    @Autowired
    private DataSource dataSource;

    @Bean
    public DaoContext context() {

        return DaoContext.builder()
                .withDefault(new LinkedHashMap<String, DataSource>() {{
                    put("main", dataSource);
                }})
                .build();

    }
}
