package test

/**
 *
 * @author afezeria
 */
class StringResultTypeHandler {
    companion object {
        @JvmStatic
        fun handleResult(obj: Any?, clazz: Class<*>): String {
            return obj.toString()
        }
    }
}

class CharacterResultTypeHandler {
    companion object {
        @JvmStatic
        fun handleResult(obj: Any?, clazz: Class<*>): Char? {
            return obj?.toString()?.get(0)
        }
    }
}


class Enum2StringParameterTypeHandler {
    companion object {
        @JvmStatic
        fun handleParameter(e: Enum<*>?): Any? {
            return e?.name
        }
    }
}

class PersonTypeResultTypeHandler {
    companion object {
        @JvmStatic
        fun handleResult(obj: Any?, clazz: Class<*>): PersonType? {
            return if (obj == null) {
                null
            } else {
                PersonType.valueOf(obj.toString())
            }
        }
    }
}