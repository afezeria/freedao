package test

import java.time.LocalDateTime


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

class LocalDateTimeGenerator {
    companion object {
        var dateTime: LocalDateTime? = null

        @JvmStatic
        fun gen(obj: Any, name: String, type: Class<*>): Any {
            return dateTime ?: LocalDateTime.now()
        }
    }
}
