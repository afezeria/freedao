package test

import io.github.afezeria.freedao.annotation.Column
import io.github.afezeria.freedao.annotation.Table
import io.github.afezeria.freedao.classic.runtime.AutoFill

/**
 *
 * @author afezeria
 */
@Table(
    name = "person",
    primaryKeys = ["id"]
)
class OnePropertyEntity(
    @Column(insert = false)
    @AutoFill
    var id: Long? = null,
) {
}