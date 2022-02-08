package expect;

import com.github.afezeria.freedao.annotation.Table;
import com.github.afezeria.freedao.annotation.Column;

/**
 */
@Table(
        name = "person",
        primaryKeys = {"id"}
)
public class Person {
    @Column
    private Long id;
    @Column
    private Integer age;
    @Column
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
