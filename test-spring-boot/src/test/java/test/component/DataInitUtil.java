package test.component;

import io.github.afezeria.freedao.classic.runtime.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author afezeria
 */
@Component
public class DataInitUtil {
    @Autowired
    JdbcTemplate template;
    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    DataInitUtil self;

    public void init() {
        self.initMaster1();
        self.initMaster2();
    }

    public static int tableSize = 10;

    /**
     * 因为两个数据源用的是一个库，只需要初始化一次
     */
    @DS(Db.MASTER_1)
    public void initMaster1() {
        template.execute("""
                drop table if exists t_order;
                create table t_order
                (
                    id           serial primary key,
                    name         text,
                    when_created timestamp default now()
                );
                drop table if exists t_order_item;
                create table t_order_item
                (
                    id serial primary key,
                    order_id  int,
                    name      text
                );
                """);
        template.batchUpdate(
                """
                        insert into t_order (name)
                        values (?)
                        """,
                IntStream.rangeClosed(1, tableSize)
                        .mapToObj(i -> new Object[]{"a" + i})
                        .collect(Collectors.toList())
        );
        template.batchUpdate(
                """
                        insert into t_order_item (order_id, name)
                        values (?, ?)
                        """,
                IntStream.rangeClosed(1, tableSize + (tableSize / 2))
                        .mapToObj(i -> new Object[]{i > tableSize ? i - tableSize : i, "b" + i})
                        .collect(Collectors.toList())
        );
        template.execute("""
                drop table if exists t_user;
                create table t_user
                (
                    id bigserial primary key ,
                    name text
                );
                insert into t_user (name) values ('master1');
                """);
    }

    @DS(Db.MASTER_2)
    public void initMaster2() {
        template.execute("""
                drop table if exists temp_test_table;
                create table temp_test_table
                (
                    name         text primary key
                );
                insert into temp_test_table (name) values ('a'),('b'),('c');
                """);

        template.execute("""
                drop table if exists t_user;
                create table t_user
                (
                    id bigserial primary key ,
                    name text
                );
                insert into t_user (name) values ('master2');
                """);
    }
}
