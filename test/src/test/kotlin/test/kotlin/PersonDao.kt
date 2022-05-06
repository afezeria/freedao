package test.kotlin;

import io.github.afezeria.freedao.annotation.Dao
import test.Person


/**
 * @author afezeria
 */
@Dao(
    crudEntity = Person::class
)
interface PersonDao {
    fun queryByIdGreaterThan(id: Long): List<Person>

    fun list(order: Person?): List<Person>

    fun insert(order: Person): Int

    fun count(order: Person?): Int

    fun selectOneById(id: Long): Person
}
