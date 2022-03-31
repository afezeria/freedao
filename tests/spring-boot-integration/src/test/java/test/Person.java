package test;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.runtime.classic.AutoFill;
import lombok.Data;

import java.time.LocalDateTime;

@Table(primaryKeys = {"id", "name"})
@Data
public class Person {

    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;
    private Boolean active;
    private LocalDateTime whenCreated;
}