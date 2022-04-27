package test

import io.github.afezeria.freedao.classic.runtime.AutoFill

/**
 *
 * @author afezeria
 */
@io.github.afezeria.freedao.annotation.Table(
    name = "person",
    primaryKeys = ["id"]
)
class OnePropertyEntity(
    @io.github.afezeria.freedao.annotation.Column(insert = false)
    @AutoFill
    var id: Long? = null,
) {
}