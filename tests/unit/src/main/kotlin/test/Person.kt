package test

import com.github.afezeria.freedao.annotation.Column
import com.github.afezeria.freedao.annotation.Table
import com.github.afezeria.freedao.runtime.classic.AutoFill
import java.time.LocalDateTime

/**
 *
 * @author afezeria
 */
@DDL(
    dialect = "my",
    value = """
create table `person`
(
    `id`   int auto_increment primary key,
    `name` text,
    `active` bool,
    `when_created` timestamp default now()
)
    """
)
@DDL(
    dialect = "pg",
    value = """
create table "person"
(
    "id"          bigserial primary key,
    "name"        text,
    "active"      bool,
    "when_created" timestamp default now()
)
    """
)
@Table(
    name = "person",
    primaryKeys = ["id"]
)
class Person(
    @Column(insert = false)
    @AutoFill
    var id: Long? = null,
    var name: String? = null,
    var whenCreated: LocalDateTime? = null,
    var active: Boolean? = null,
) : Entity {

}

interface Entity
