package com.github.afezeria.freedao.processor.core

/**
 *
 */
lateinit var global: GlobalState

class GlobalState(
    val debug: Boolean,
    val quote: String,
) {

    companion object {
        fun init() {
            val prefix = "freedao"
            processingEnvironment.options.apply {
                global = GlobalState(
                    debug = get("$prefix.debug")?.toBoolean() ?: false,
                    quote = get("$prefix.quote") ?: "\""
                )
            }
        }
    }
}