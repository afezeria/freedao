package test

import io.github.afezeria.freedao.annotation.Table

/**
 *
 * @author afezeria
 */
@DDL(
    dialect = "mysql", value = """
create table `clazz`
(
    `id`   int,
    `teacher_id` int,
    `name` varchar(200),
    primary key(`id`,`teacher_id`)
)
    """
)
@DDL(
    dialect = "pg", value = """
create table "clazz"
(
    "id"   int,
    "teacher_id" int,
    "name" varchar(200),
    primary key("id","teacher_id")

)
    """
)
@Table(name = "clazz", primaryKeys = ["id", "teacher_id"])
open class Clazz(
    open var id: Int? = null,
    open var teacherId: Int? = null,
    open var name: String? = null,
) : Entity
