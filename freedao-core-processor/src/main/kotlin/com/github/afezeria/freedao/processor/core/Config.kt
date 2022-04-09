package com.github.afezeria.freedao.processor.core

/**
 *
 */
object GlobalConfig {
    var placeholderTemplate: String = "?"
    var placeholderStartIdx: String = "1"
}

class PlaceholderGen {
    var counter = GlobalConfig.placeholderStartIdx.toInt()

    fun gen(): String {
        val res = GlobalConfig.placeholderTemplate.replace("counter", counter++.toString())
        if (res != GlobalConfig.placeholderTemplate) {
            return res
        }
        return res
    }
}