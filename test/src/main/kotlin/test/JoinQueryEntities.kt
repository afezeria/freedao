package test

import io.github.afezeria.freedao.annotation.Column
import io.github.afezeria.freedao.annotation.Table
import io.github.afezeria.freedao.classic.runtime.AutoFill
import io.github.afezeria.freedao.classic.runtime.Join
import io.github.afezeria.freedao.classic.runtime.ReferenceValue

/**
 *
 * @author afezeria
 */
@DDL(
    dialect = "mysql", value = """
create table `ta`
(
    `id`   long auto_increment primary key,
    `name` varchar(200),
    `p_id` long
)
    """
)
@DDL(
    dialect = "pg", value = """
create table "ta"
(
    "id"          bigserial primary key,
    "name"        varchar(200),
    "p_id"         bigint
)
    """
)
@Table(name = "ta", primaryKeys = ["id"])
@Join(id = "_p", foreignKey = ["p_id"], entityClass = Person::class)
open class JoinEntityA(
    @field:Column(insert = false) @field:AutoFill open var id: Long? = null,
    open var name: String? = null,
    @field:Column
    open var pId: Long? = null,
    @field:Column(exist = false)
    @ReferenceValue(joinId = "_p", columnName = "name")
    open var pName: String? = null,
) : Entity
