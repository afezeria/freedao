package test.component;

import com.github.afezeria.freedao.spring.runtime.FreedaoProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;

/**
 * @author afezeria
 */
@Component
public class DataInitUtil {
    @Autowired
    FreedaoProperties properties;

    public static int tableSize = 20;

    @SneakyThrows
    public void init() {
        for (Map.Entry<String, HikariDataSource> entry : properties.getDatasource().entrySet()) {
            HikariDataSource dataSource = entry.getValue();
            try (Connection conn = dataSource.getConnection()) {
                if (Objects.equals(entry.getKey(), Db.MASTER_1)) {
                    conn.prepareStatement("drop table if exists person").execute();
                    conn.prepareStatement("""
                            create table "person"
                            (
                                "id"           serial primary key,
                                "name"         text,
                                "active"       bool,
                                "when_created" timestamp default now()
                            )
                            """).execute();
                    for (int i = 0; i < tableSize; i++) {
                        PreparedStatement preparedStatement = conn.prepareStatement("""
                                insert into person(name, active)
                                values (?, true)
                                """);
                        preparedStatement.setObject(1, "a" + i);
                        preparedStatement.execute();
                    }
                } else if (Objects.equals(entry.getKey(), Db.MASTER_2)) {
                    conn.prepareStatement("drop table if exists clazz").execute();
                    conn.prepareStatement("""
                            create table "clazz"
                            (
                                "id"   bigserial primary key,
                                "name" text
                            )
                            """).execute();
                    for (int i = 0; i < tableSize; i++) {
                        PreparedStatement preparedStatement = conn.prepareStatement("""
                                insert into clazz(name)
                                values (?)
                                """);
                        preparedStatement.setObject(1, "a" + i);
                        preparedStatement.execute();
                    }
                }
            }
        }
    }
}
