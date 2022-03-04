package test

/**
 *
 * @author afezeria
 */
class Object2StringParameterTypeHandler {
    companion object {
        @JvmStatic
        fun handle(e: Any?): Any {
            return e.toString()
        }
    }
}