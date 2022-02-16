package test.java.style.crud.insert;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.runtime.classic.AutoFill;

import java.time.LocalDateTime;

/**
 *
 */
@Table(
        name = "person",
        primaryKeys = "id"
)
public class PersonInsert {
    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;

    private LocalDateTime createDate;

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PersonInsert{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
