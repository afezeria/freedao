package test.java.integration.classic.runtime;

import io.github.afezeria.freedao.annotation.Column;
import io.github.afezeria.freedao.annotation.Join;
import io.github.afezeria.freedao.annotation.ReferenceValue;
import io.github.afezeria.freedao.annotation.Table;
import io.github.afezeria.freedao.classic.runtime.AutoFill;
import lombok.Data;
import test.DDL;
import test.Entity;
import test.Person;

/**
 * @author afezeria
 */
@DDL(
        dialect = "mysql", value = """
        create table `ta`
        (
            `id`   long auto_increment primary key,
            `name` varchar(200),
            `p_id` long
        )
            """
)
@DDL(
        dialect = "pg", value = """
        create table "ta"
        (
            "id"          bigserial primary key,
            "name"        varchar(200),
            "p_id"         bigint
        )
            """
)
@Table(name = "ta", primaryKeys = {"id"})
@Join(id = "_p", foreignKey = {"p_id"}, entityClass = Person.class)
@Data
public class JoinEntityA implements Entity {
    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;
    @Column(name = "p_id")
    private Long personId;
    @Column(exist = false)
    @ReferenceValue(joinId = "_p", columnName = "name")
    private String personName;

}