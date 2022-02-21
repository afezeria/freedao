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
    dialect = "mysql",
    value = """
create table `person`
(
    `id`   long auto_increment primary key,
    `name` varchar(200),
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
    "name"        varchar(200),
    "active"      bool,
    "when_created" timestamp default now()
)
    """
)
@Table(
    name = "person",
    primaryKeys = ["id"]
)
open class Person(
    @Column(insert = false)
    @AutoFill
    var id: Long? = null,
    var name: String? = null,
    var whenCreated: LocalDateTime? = null,
    var active: Boolean? = null,
) : Entity

interface Entity

@Table(
    name = "person",
    primaryKeys = ["id"]
)
class PersonOverrideEqual : Person() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name?.hashCode() ?: 0
    }
}

@Table(
    name = "person",
    primaryKeys = ["id"]
)
class PersonWithoutPublicConstructor private constructor() : Person()

@Table(
    name = "person",
    primaryKeys = ["id"]
)
class PersonBad1(test: Number) : Person()

@Table(
    name = "person",
    primaryKeys = ["id"]
)
class PersonWithRequiredId(id: Long) : Person() {
    init {
        this.id = id
    }
}
