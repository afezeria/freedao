package test

import io.github.afezeria.freedao.Long2IntegerResultHandler
import io.github.afezeria.freedao.annotation.Column
import io.github.afezeria.freedao.annotation.Table

/**
 *
 * @author afezeria
 */
class NoProperty {

}

class WithoutTableAnnotation {
    var name: String? = null
}

class ExtendMap : HashMap<String, Any>() {

}

class ExtendList : ArrayList<String>() {

}

@Table(name = "error")
class InvalidParameterTypeHandlerEntity(
    @field:Column(parameterTypeHandle = Enum2StringParameterTypeHandler::class)
    val id: String
)

@Table(name = "error")
class InvalidResultTypeHandlerEntity(
    @field:Column(resultTypeHandle = Long2IntegerResultHandler::class)
    val id: String
)
