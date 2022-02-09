package test.java.jpa;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.runtime.classic.AutoFill;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 */
@Table(
        name = "person",
        primaryKeys = "id"
)
@Data
public class PersonFindById {
    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;

    private LocalDateTime createDate;

}
