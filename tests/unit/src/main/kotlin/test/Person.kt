package test

import com.github.afezeria.freedao.annotation.Column
import com.github.afezeria.freedao.annotation.Table
import com.github.afezeria.freedao.runtime.classic.AutoFill
import java.time.LocalDateTime

/**
 *
 * @author afezeria
 */

interface Entity

@DDL(
    dialect = "mysql", value = """
create table `person`
(
    `id`   long auto_increment primary key,
    `name` varchar(200),
    `active` bool,
    `type`   varchar(20), 
    `age`         int,
    `nick_name`   varchar(200),
    `when_created` timestamp default now()
)
    """
)
@DDL(
    dialect = "pg", value = """
create table "person"
(
    "id"          bigserial primary key,
    "name"        varchar(200),
    "active"      bool,
    "type"      varchar(20), 
    "age"         int,
    "nick_name"   varchar(200),
    "when_created" timestamp default now()
)
    """
)
@Table(name = "person", primaryKeys = ["id"])
open class Person(
    @field:Column(insert = false) @field:AutoFill open var id: Long? = null,
    open var name: String? = null,
    open var active: Boolean? = null,
    open var whenCreated: LocalDateTime? = null,
    open var age: Int? = null,
    open var nickName: String? = null,
    @Column(
        name = "age",
        insert = false,
        update = false,
        resultTypeHandle = StringResultTypeHandler::class
    )
    open var stringAge: String? = null,
    @Column(
        parameterTypeHandle = Enum2StringParameterTypeHandler::class,
        resultTypeHandle = PersonTypeResultTypeHandler::class,
    )
    open var type: PersonType? = null,
    @Column(
        name = "type",
        insert = false,
        update = false,
    )
    open var searchType: PersonType? = null

) : Entity

enum class PersonType {
    TEACHER, STUDENT
}


@Table(name = "person", primaryKeys = ["id"])
class PersonWithConstructor(
    var id: Long?,
    @Column(resultTypeHandle = StringResultTypeHandler::class) var name: String?,
    var active: Any? = null,
) {
    var whenCreated: LocalDateTime? = null

    @Column(name = "nick_name", resultTypeHandle = StringResultTypeHandler::class)
    var alias: String? = null

    @Column(name = "nick_name", resultTypeHandle = StringResultTypeHandler::class)
    val alias2: String? = null
    var age: Any? = null
}

@Table(name = "person", primaryKeys = ["id"])
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

@Table(name = "person", primaryKeys = ["id"])
class PersonWithoutPublicConstructor private constructor() : Person()

@Table(name = "person", primaryKeys = ["id"])
class PersonBad1(test: Number) : Person()

@Table(name = "person", primaryKeys = ["id"])
class PersonWithRequiredId(id: Long) : Person() {
    init {
        this.id = id
    }
}

@Table(
    name = "person_log",
)
class PersonWithoutPrimaryKey(
    var personId: Long? = null,
    var action: String? = null,
)

@Table(name = "person", primaryKeys = ["id"])
open class PersonStringId(
    @Column(insert = false, resultTypeHandle = StringResultTypeHandler::class) @AutoFill var id: String? = null,
    var name: String? = null,
)

@Table(name = "person", primaryKeys = ["id"])
open class PersonAnyId(
    @Column(insert = false) @AutoFill var id: Any? = null,
    var name: String? = null,
)

@Table(name = "person", primaryKeys = ["id"])
open class PersonWithoutInsertableProperty(
    @Column(insert = false) var id: Any? = null,
    @Column(insert = false) var name: String? = null,
)

data class PersonIdAndNameDto(
    val id: Long,
) {
    var name: String? = null
    var fieldNotInEntity: String? = null

    //同样的名字不同类型字段会被过滤
    var nickName: Int? = null
}

data class PersonStringAgeDto(
    val stringAge: String?,
)

data class DtoNoCommonFieldWithPerson(val `abcdefgh`: String)
