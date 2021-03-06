package test.component;

import io.github.afezeria.freedao.annotation.Column;
import io.github.afezeria.freedao.annotation.Table;
import io.github.afezeria.freedao.classic.runtime.AutoFill;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "t_order", primaryKeys = {"id"})
@Data
public class Order {
    @Column(insert = false)
    @AutoFill
    private Integer id;
    private String name;
    @AutoFill
    @Column(insert = false)
    private LocalDateTime whenCreated;
}