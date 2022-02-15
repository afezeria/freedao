package test;

import com.github.afezeria.freedao.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Table(primaryKeys = {"id", "name"})
@Data
public class Person {

    private Long id;
    private String name;
    private Boolean active;
    private LocalDateTime whenCreated;
}