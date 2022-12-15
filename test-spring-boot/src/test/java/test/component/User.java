package test.component;

import io.github.afezeria.freedao.annotation.Column;
import io.github.afezeria.freedao.annotation.Table;
import io.github.afezeria.freedao.classic.runtime.AutoFill;
import lombok.Data;

/**
 * @author afezeria
 */
@Table(name = "t_user", primaryKeys = {"id"})
@Data
public class User {
    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;
}
