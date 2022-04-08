package test.component;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.classic.runtime.AutoFill;
import lombok.Data;

import java.time.LocalDateTime;

@Table(primaryKeys = {"id", "name"})
@Data
public class Person {

    @Column(insert = false)
    @AutoFill
    private Integer id;
    private String name;
    private Boolean active;
    private LocalDateTime whenCreated;
}