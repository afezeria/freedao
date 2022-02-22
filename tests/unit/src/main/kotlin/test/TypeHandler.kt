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