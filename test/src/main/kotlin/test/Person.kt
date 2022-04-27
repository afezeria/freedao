package test

import io.github.afezeria.freedao.classic.runtime.AutoFill
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
@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
open class Person(
    @field:io.github.afezeria.freedao.annotation.Column(insert = false) @field:AutoFill open var id: Long? = null,
    open var name: String? = null,
    open var active: Boolean? = null,
    open var whenCreated: LocalDateTime? = null,
    open var age: Int? = null,
    open var nickName: String? = null,
    @io.github.afezeria.freedao.annotation.Column(
        name = "age",
        insert = false,
        update = false,
        resultTypeHandle = StringResultTypeHandler::class
    )
    open var stringAge: String? = null,
    @io.github.afezeria.freedao.annotation.Column(
        parameterTypeHandle = Enum2StringParameterTypeHandler::class,
        resultTypeHandle = PersonTypeResultTypeHandler::class,
    )
    open var type: PersonType? = null,
    @io.github.afezeria.freedao.annotation.Column(
        name = "type",
        insert = false,
        update = false,
    )
    open var searchType: PersonType? = null

) : Entity

enum class PersonType {
    TEACHER, STUDENT
}


@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
class PersonWithConstructor(
    var id: Long?,
    @io.github.afezeria.freedao.annotation.Column(resultTypeHandle = StringResultTypeHandler::class) var name: String?,
    var active: Any? = null,
) {
    var whenCreated: LocalDateTime? = null

    @io.github.afezeria.freedao.annotation.Column(name = "nick_name", resultTypeHandle = StringResultTypeHandler::class)
    var alias: String? = null

    @io.github.afezeria.freedao.annotation.Column(name = "nick_name", resultTypeHandle = StringResultTypeHandler::class)
    val alias2: String? = null
    var age: Any? = null
}

@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
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

@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
class PersonWithoutPublicConstructor private constructor() : Person()

@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
class PersonBad1(test: Number) : Person()

@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
class PersonWithRequiredId(id: Long) : Person() {
    init {
        this.id = id
    }
}

@io.github.afezeria.freedao.annotation.Table(
    name = "person_log",
)
class PersonWithoutPrimaryKey(
    var personId: Long? = null,
    var action: String? = null,
)

@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
open class PersonAnyId(
    @io.github.afezeria.freedao.annotation.Column(insert = false) @AutoFill var id: Any? = null,
    var name: String? = null,
)

@io.github.afezeria.freedao.annotation.Table(name = "person", primaryKeys = ["id"])
open class PersonWithoutInsertableProperty(
    @io.github.afezeria.freedao.annotation.Column(insert = false) var id: Any? = null,
    @io.github.afezeria.freedao.annotation.Column(insert = false) var name: String? = null,
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

@DDL(
    dialect = "mysql",
    value = """
create table `auto_fill_int_id`
(
    `id`   long auto_increment primary key,
    `name` varchar(200),
    `when_created` timestamp default now(),
    `when_updated` timestamp default now()
)
    """
)
@DDL(
    dialect = "pg",
    value = """
create table "auto_fill_int_id"
(
    "id"          bigserial primary key,
    "name"        varchar(200),
    "when_created" timestamp default now(),
    "when_updated" timestamp default now()
)
    """
)
@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
open class AutoFillEntity(
    open var id: Long? = null,
    open var name: String? = null,
    open var whenCreated: LocalDateTime? = null,
    open var whenUpdated: LocalDateTime? = null,
) : Entity

@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
open class FillObjectTypeFieldByDbGeneratedKeyEntity(
    @io.github.afezeria.freedao.annotation.Column(insert = false) @AutoFill
    var id: Any? = null,
    var name: String? = null,
) : Entity

@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
open class FillValueHandledByTypeHandler(
    @io.github.afezeria.freedao.annotation.Column(insert = false, resultTypeHandle = StringResultTypeHandler::class)
    @AutoFill
    var id: String? = null,
    var name: String? = null,
) : Entity


@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
class DbGeneratedKeyEntity(
    @field:io.github.afezeria.freedao.annotation.Column(insert = false)
    @field:AutoFill
    override var id: Long? = null,
) : AutoFillEntity()

@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
class MultiDbGeneratedKeysEntity(
    @field:io.github.afezeria.freedao.annotation.Column(insert = false)
    @field:AutoFill
    override var id: Long? = null,
    @field:AutoFill
    @field:io.github.afezeria.freedao.annotation.Column(insert = false)
    override var whenCreated: LocalDateTime? = null,
) : AutoFillEntity()

@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
class CustomGeneratorEntity(
    @field:io.github.afezeria.freedao.annotation.Column(insert = false)
    @field:AutoFill(
        before = true,
        generator = NegativeLongIdGenerator::class
    )
    override var id: Long? = null,
) : AutoFillEntity()

@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
class MultipleFieldCustomGeneratorEntity(
    @field:io.github.afezeria.freedao.annotation.Column(insert = false)
    @field:AutoFill(
        before = true,
        generator = NegativeLongIdGenerator::class
    )
    override var id: Long? = null,
    @field:io.github.afezeria.freedao.annotation.Column(insert = false)
    @field:AutoFill(
        before = true,
        update = true,
        generator = LocalDateTimeGenerator::class
    )
    override var whenUpdated: LocalDateTime? = null,
) : AutoFillEntity()

@io.github.afezeria.freedao.annotation.Table(name = "auto_fill_int_id", primaryKeys = ["id"])
class FillByCustomGeneratorWhenUpdateEntity(
    override var id: Long? = null,
    override var name: String? = null,
    @field:AutoFill(before = true, update = true, generator = LocalDateTimeGenerator::class)
    override var whenUpdated: LocalDateTime? = null,
) : AutoFillEntity()
