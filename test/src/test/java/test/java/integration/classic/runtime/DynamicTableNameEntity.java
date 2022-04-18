package test.java.integration.classic.runtime;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.classic.runtime.AutoFill;
import lombok.Data;
import lombok.experimental.Accessors;
import test.DDL;
import test.Entity;

/**
 * @author afezeria
 */
@DDL(
        dialect = "mysql", value = """
        create table `person2000`
        (
            `id`   long auto_increment primary key,
            `name` varchar(200)
        )
        """
)
@DDL(
        dialect = "pg", value = """
        create table "person2000"
        (
            "id"          bigserial primary key,
            "name"        varchar(200)
        )
        """
)
@Table(name = "person${_context.\"year\"}")
@Data
@Accessors(chain = true)
public class DynamicTableNameEntity implements Entity {
    @AutoFill
    @Column(insert = false)
    private Long id;
    private String name;
}
