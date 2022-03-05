package test

/**
 *
 * @author afezeria
 */
class StringResultTypeHandler {
    companion object {
        @JvmStatic
        fun handle(obj: Any?): String {
            return obj.toString()
        }
    }
}

class CharacterResultTypeHandler {
    companion object {
        @JvmStatic
        fun handle(obj: Any?): Char? {
            return obj?.toString()?.get(0)
        }
    }
}

class Enum2StringResultTypeHandler {
}

class Enum2StringParameterTypeHandler {
    companion object {
        @JvmStatic
        fun handle(e: Enum<*>?): Any? {
            return e?.name
        }
    }
}

class PersonTypeResultTypeHandler {
    companion object {
        @JvmStatic
        fun handle(obj: Any?): PersonType? {
            return if (obj == null) {
                null
            } else {
                PersonType.valueOf(obj.toString())
            }
        }
    }
}