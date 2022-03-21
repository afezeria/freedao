package test


class NegativeLongIdGenerator {
    companion object {
        var i = -1L

        fun reset() {
            i = -1L
        }

        @JvmStatic
        fun gen(obj: Any, name: String, type: Class<*>): Any {
            return i--
        }
    }
}