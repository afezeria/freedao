package test.java.crud.count;

import com.github.afezeria.freedao.annotation.Column;
import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.runtime.classic.AutoFill;

/**
 */
@Table(
        name = "person",
        primaryKeys = "id"
)
public class PersonCount {
    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;

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
