# 标准CRUD方法

说明：

- DAO接口的`Dao.crudEntity`必须指定且不能为Object
- 所有方法的实体类参数除特殊说明不能null

支持的方法：

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