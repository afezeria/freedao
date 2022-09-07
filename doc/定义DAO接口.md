# DAO接口声明

说明：

- 接口必须是顶层接口，不能是内部接口或静态内部接口
- 接口必须使用`io.github.afezeria.freedao.annotation.Dao`注解
- freedao生成实现类时只会实现抽象方法，不会处理静态方法，有必要的话可以通过静态方法来做一些特殊处理

方法声明规则请参照[方法声明](home.md#DAO方法声明)

--- 
示例：

```java

@Dao(crudEntity = JoinEntityA.class)
public interface PersonDao {
    List<Person> list(Person person);
}

```