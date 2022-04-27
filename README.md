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

## 实体类声明

实体类必须是java bean，并且用`Table`注释：

```java

@Table(name = "person", primaryKeys = {"id"})
public class Person {
    @Column(insert = false)
    @AutoFill
    private Long id;
    private String name;
    private Boolean active;
    private LocalDateTime whenCreated;
    private Integer age;
    private String nickName;

    //getter and setter 
}

```

## 接口声明

dao接口必须是一个用`Dao`注释的顶层接口，`Dao.crudEntity`是可选的，其值必须是实体类，未指定时接口中只能声明xml方法。dao接口不能有类型参数。

例：

```java

@Dao(crudEntity = Person.class)
public interface PersonDao {
    Person selectOneById(@NotNull Person person);
}
```

## 方法声明

dao方法必须是抽象方法且不能声明范型参数，可选的方法有三种：命名方法、crud方法和xml方法

### 命名方法

风格和 Spring Data JPA 类似，根据方法名生成查询方法。

前缀：

| 关键字                         | 示例              | 说明          |
|:----------------------------|:----------------|:------------|
| (select/query/find)By       | selecetByName   | 查询并返回实体类列表  |
| (select/query/find)OneBy    | queryOneById    | 查询并返回实体类    |
| dto(Select/Query/Find)By    | dtoSelectByName | 查询并返回DTO列表  |
| dto(Select/Query/Find)OneBy | dtoQueryOneById | 查询并返回DTO    |
| (delete/remove)By           | deleteById      | 根据条件删除      |
| countBy                     | countByName     | 返回符合查询条件的行数 |

条件关键字：

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

连接关键字：

| 关键字 | 示例              | sql                               |
|:----|:----------------|:----------------------------------|
| And | findByIdAndName | where id = #{p1} and name = #{p2} |
| Or  | findByIdOrName  | where id = #{p1} or name = #{p2}  |

排序关键字：

| 关键字     | 示例                                    | sql                                                 |
|:--------|:--------------------------------------|:----------------------------------------------------|
| OrderBy | findByNameOrderIdAscAndCreateDateDesc | where name = #{p} order by id asc, create_date desc |

注意：排序字段后的Asc/Desc不能省略

### CRUD方法

所有方法的实体类参数除特殊说明不能null。

示例：

```java

@Dao(crudEntity = Person.class)
class PersonDao {
    /**
     * 返回总行数
     * 根据person中所有非空字段做等值查询
     * 返回值可以为Integer或Long
     * @param person nullable 为null时查询所有数据
     */
    Integer count(Person person);

    /**
     * 返回所有数据
     * 根据person中所有非空字段做等值查询
     * 返回值的容器类型可以是List/Set/Collection或者Collection接口的某个具体实现类
     * 当容器为接口时结果的实际类型：
     * List         -> ArrayList
     * Set          -> HashSet
     * Collection   -> ArrayList
     * @param person nullable 为null时查询所有数据
     * @return 实体类列表
     */
    List<Person> list(Person person);

    /**
     * 根据删除
     * 根据person中所有非空字段做等值查询
     * 返回值可以为Integer或Long
     * @return 受影响的行数
     */
    Integer delete(Person person);

    /**
     * 新增并插入所有字段
     * 实体类必须有可插入字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     */
    Integer insert(Person person);

    /**
     * 新增并插入所有值不为null的字段
     * 实体类必须有可插入字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     */
    Integer insertNonNullFields(Person person);

    /**
     * 根据id更新所有字段
     * 实体类必须有除主键外的可更新字段字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     * @apiNote
     */
    Integer update(Person person);

    /**
     * 根据id更新所有值不为null的字段
     * 实体类必须有除主键外的可更新字段字段
     * 返回值可以为Integer或Long
     * @param person 实体类
     * @return 受影响的行数
     */
    Integer updateNonNullFields(Person person);
}
```

### Xml方法

基于xml模板生成查询方法，

示例：

```java

@Dao
class PersonXmlDao {

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

节点类型可以自行定义，只需要实现`io.github.afezeria.freedao.processor.core.template.XmlElement`且在编译期可以通过ServiceLoader加载

以下示例中的结果不考虑换行符

##### if

当test表达式的结果为true时将节点中的内容到sql

xml:

```xml

<select>
    <select>
        select * from person
        <if test="id != null">
            where id = #{id}
        </if>
    </select>
</select>
```

结果：

```sql
select *
from person
where id = ?
```

##### foreach

循环指定集合并将内容拼接到sql

| 属性         | 说明            | 必填    |
|:-----------|:--------------|:------|
| collection | 指定要遍历的循环      | true  |
| item       | 声明保存循环元素的属性名  | true  |
| index      | 声明保存循环计数器的属性名 | false |
| open       | 要添加到循环之间的字符串  | false |
| open       | 要添加到循环之后的字符串  | false |
| separator  | 每次循环后添加的分隔符   | false |

```xml

<select>
    select * from person where id in
    <foreach collection="list"
             item="it"
             open="(" close=")" separator=",">
        #{it}
    </foreach>
</select>
```

结果：

```sql
select *
from person
where id in (?, ?, ?)
```

##### trim

当内容的节点不为空时，从内容的开头和结尾移除空字符串和指定的字符，并将prefix添加到开头

| 属性              | 说明          | 必填    |
|-----------------|-------------|-------|
| prefix          | 要添加到开头的字符串  | false |
| prefixOverrides | 要从内容开头移除的文本 | true  |
| suffixOverrides | 要从内容结尾移除的文本 | true  |

注意：prefixOverrides和suffixOverrides的内容是区分大小写的，多个要移除的字符串之间用`|`分隔

```xml

<select>
    select * from person
    <trim prefix="where " prefixOverrides="and |or ">
        <if test="id != null">
            and id = #{id}
        </if>
        <if test="name != null">
            or name like #{name}
        </if>
    </trim>
</select>
```

结果：

```sql
-- id == null and name == null
select *
from person
-- id != null and name == null
select *
from person
where id = ?
-- id == null and name != null
select *
from person
where name like ?
```

##### where

等价于

```xml

<trim prefix="where " prefixOverrides="and |or " suffixOverrides="">
</trim>
```

##### set

等价于

```xml

<trim prefix="where " prefixOverrides="" suffixOverrides=",">
</trim>
```

##### choose

和switch语句类似，选择多个分支中的一个并忽略其他的分支

```xml

<select>
    select * from person where 1 = 1
    <choose>
        <when test="id != null">
            and id != #{id}
        </when>
        <when test="name != null">
            and name like #{name}
        </when>
        <otherwise>
            and age > 1
        </otherwise>
    </choose>
</selectkj>
```

结果：

```sql
-- id != null and name != null
select *
from person
where 1 = 1
  and id != ?
-- id == null and name != null
select *
from person
where 1 = 1
  and name like ?
-- id == null and name == null
select *
from person
where 1 = 1
  and age > 1
```

##### when

只能作为choose的直接子节点出现，和if类似，test表达式结果为真时拼接内容到sql并忽略choose中的其他节点

##### otherwise

只能作为choose的直接子节点出现，当没有所有when节点都没被选中时执行otherwise节点中的内容

#### test表达式和占位符

##### 属性表达式

用于占位符、test表达式和foreach节点的collection属性。

规则：

1. 访问java bean的可读属性：`a.b`
2. 访问list元素：`a.1`
3. 访问map的值：`a."key"`
4. 访问list或map的大小： `a.size`

注意：

- 不支持通过位置索引引用方法参数
- 当无法确定`a`的类型时，`a.b`会在运行时使用反射查找`getB`方法
- 同一个属性在整个xml中只能声明一次，即使他们的作用域不重叠，声明属性的方式：
  - 方法参数
  - foreach的item和index

下面用INVOKE_CHAIN表示属性表达式

##### 占位符

展位符分两种，格式如下

- 字符串占位符：`${INVOKE_CHAIN}`
- sql参数占位符：`#{INVOKE_CHAIN}`,`#{INVOKE_CHAIN,typeHandler=xxx.xxx.XxxHandler}`

字符串占位符在拿到expr的值后会直接调用`Object.toString`方法将结果拼接到sql中，sql参数占位符会将命名的占位符替换成具体的数据库驱动的占位符格式，并将expr的值作为参数提供给驱动。

##### test表达式

因为目标是尽可能的将表达式转换成静态的java代码，所以和其他框架中的表达式比起来存在较多限制 ，具体规则如下：

支持的常量类型及对应的写法：

| 类型        | 名称      | const      |
|:----------|---------|:-----------|
| Long      | NUMBER  | 52L,-52L   |
| Integer   | NUMBER  | 24,-24     |
| Double    | NUMBER  | 2.4,-2.4   |
| String    | TEXT    | "abc"      |
| Character | CHAR    | 'a'        |
| null      | NULL    | null       |
| Boolean   | BOOLEAN | true,false |

支持的操作符：

| 说明    | 名称            | 写法         |
|-------|---------------|------------|
| 等值比较  | EQUAL_OP      | ==,!=      |
| 大小比较  | COMPARISON_OP | \>,>=,<,<= |
| 逻辑运算符 | LOGICAL_OP    | and,or     |

一个最简单的test表达式由三部分组成，以下是可能的四种组合

1. INVOKE_CHAIN (EQUAL_OP|COMPARISON_OP) (NUMBER|TEXT|CHAR)
2. INVOKE_CHAIN (EQUAL_OP|COMPARISON_OP|LOGICAL_OP) INVOKE_CHAIN
3. INVOKE_CHAIN EQUAL_OP NULL
4. INVOKE_CHAIN LOGICAL_OP BOOLEAN

一个复杂的表达式由两个表达式加上逻辑运算符组成： expr LOGICAL_PO expr

所有的符号都是左结合的，为了方便阅读，可以在表达式的两端加上小括号

示例：

```
(a.g == b ) and c > 1L and (b and d and e <= -1.2) and (f == null) and g or true and (c.1."uaoe-_u".c.2424 > 1)
```