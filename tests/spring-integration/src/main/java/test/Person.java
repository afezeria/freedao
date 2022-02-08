package test;

import com.github.afezeria.freedao.annotation.Table;

@Table(primaryKeys = {"id", "name"})
public class Person {

    private Long id;
    private String nickname;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}