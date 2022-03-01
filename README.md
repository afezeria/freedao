# freedao

基于apt实现的简单易调试的持久层框架。

## 特性

- 运行时除slf4j外不依赖其他第三方库
- 支持方法名、xml及注解等多种方式定义查询方法
- 一定程度的编译期类型检查
- 易于调试，基于apt生成dao接口实现类，可以直接单步调试
- 对项目代码无污染，不需要显示导入自动生成的代码
- 接口实现基于原生数据库驱动，无额外的运行时开销

## 快速开始

项目当前还未正式发布，请在 `tests:spring-boot-integrate` 模块中试用

## 方法声明

### Spring Data JPA 风格的方法

根据方法名称创建查询

条件关键字

| 关键字              | 示例                       | sql                                   |
|:-----------------|:-------------------------|:--------------------------------------|
| LessThanEqual    | findByIdLessThanEqual    | where id <= #{p}                      |
| GreaterThanEqual | findByIdGreaterThanEqual | where id >= #{p}                      |
| NotNull          | findByIdNotNull          | where id not null                     |
| IsNull           | findByNameIsNull         | where name is null                    |
| LessThan         | findByIdLessThan         | where id < #{p}                       |
| GreaterThan      | findByIdGreaterThan      | where id > #{p}                       |
| NotIn            | findByIdNotIn            | where id not in (#{p[0]},#{p[1]} ...) |
| NotLike          | findByNameNotLike        | where name not like #{p}              |
| Between          | findByIdBetween          | where id between #{p1} and #{p2}      |
| Like             | findByNameLike           | where name like #{p}                  |
| Not              | findByIdNot              | where id <> #{p}                      |
| In               | findByIdIn               | where id in (#{p[0]},#{p[1]} ...)     |
| True             | findByActiveTrue         | where active = true                   |
| False            | findByActiveFalse        | where active = false                  |

连接关键字

| 关键字 | 示例              | sql                               |
|:----|:----------------|:----------------------------------|
| And | findByIdAndName | where id = #{p1} and name = #{p2} |
| Or  | findByIdOrName  | where id = #{p1} or name = #{p2}  |

排序关键字

| 关键字     | 示例                                    | sql                                                 |
|:--------|:--------------------------------------|:----------------------------------------------------|
| OrderBy | findByNameOrderIdAscAndCreateDateDesc | where name = #{p} order by id asc, create_date desc |

注意：排序字段后的Asc/Desc不能省略

### CRUD方法

声明CRUD方法的Dao接口必须指定`Dao.crudEntity`，该类型必须是一个由`Table`注解注释的java bean

示例：

```java

@Dao(crudEntity = Person.class)
class PersonDao {
    /**
     * 返回总行数
     * 返回值可以为Integer或Long
     */
    Integer count();

    /**
     * 根据主键删除
     * 返回值可以为Integer或Long
     * @param id Person主键，参数列表必须和Person中声明的主键匹配
     * @return 受影响的行数
     */
    Integer delete(Long id);

    /**
     * 新增并插入所有字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     */
    Integer insert(Person person);

    /**
     * 新增并插入所有值不为null的字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     */
    Integer insertSelective(Person person);

    /**
     * 根据id更新所有字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     * @apiNote
     */
    Integer update(Person person);

    /**
     * 根据id更新所有值不为null的字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     */
    Integer updateSelective(Person person);

    /**
     * 返回所有数据
     * 返回值的容器类型可以是List/Set/Collection或者Collection接口的某个具体实现类
     * 当容器为接口时结果的实际类型：
     * List         -> ArrayList
     * Set          -> HashSet
     * Collection   -> ArrayList
     * @return
     */
    List<Person> all();

}
```

### Mybatis 风格的方法

示例：
```java
@Dao
class PersonXmlDao{
    
    @XmlTemplate("""
            <select>
            select * from peron where id = #{id}
            </select>
            """)
    List<Person> selectById(Long id);
}
```

使用java编写Dao接口时推荐jdk版本大于15

#### 节点类型

节点类型可以自行定义，只需要实现`com.github.afezeria.freedao.processor.core.template.XmlElement`且在编译期可以通过ServiceLoader加载

##### if

当test表达式的结果为true时if节点中的内容将被拼接到sql中

```xml
<select>
    <select>
        select * from person
        <if test="id != null">
        WHERE id = #{id}
        </if>
    </select>
</select>
```
