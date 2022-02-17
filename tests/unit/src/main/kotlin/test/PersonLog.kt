package test

import com.github.afezeria.freedao.annotation.Table

/**
 *
 * @author afezeria
 */
@DDL(
    dialect = "my",
    value = """
create table `person_log`
(
    `person_id` int,
    `action` varchar(200)
)
    """
)
@DDL(
    dialect = "pg",
    value = """
create table "person_log"
(
    "person_id" bigserial,
    "action" text
)
    """
)
@Table(
    name = "person_log",
)
class PersonLog(
    var personId: Long? = null,
    var action: String? = null,
) : Entity {

}
