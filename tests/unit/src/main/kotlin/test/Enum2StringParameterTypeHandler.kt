package test

/**
 *
 * @author afezeria
 */
class Enum2StringParameterTypeHandler {
    companion object {
        @JvmStatic
        fun handle(e: Enum<*>): Any {
            return e.name
        }
    }
}