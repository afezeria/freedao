package test

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

@io.github.afezeria.freedao.annotation.Table(name = "error")
class InvalidParameterTypeHandlerEntity(
    @field:io.github.afezeria.freedao.annotation.Column(parameterTypeHandle = Enum2StringParameterTypeHandler::class)
    val id: String
)

@io.github.afezeria.freedao.annotation.Table(name = "error")
class InvalidResultTypeHandlerEntity(
    @field:io.github.afezeria.freedao.annotation.Column(resultTypeHandle = io.github.afezeria.freedao.Long2IntegerResultHandler::class)
    val id: String
)
