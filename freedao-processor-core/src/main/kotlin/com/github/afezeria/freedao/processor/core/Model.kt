package com.github.afezeria.freedao.processor.core

import com.github.afezeria.freedao.processor.core.spi.ValidatorService
import java.util.*
import javax.lang.model.element.Element

/**
 *
 */
abstract class Model<T : Element>(val element: T) {
//    private val _customData: MutableMap<String, Any?> = mutableMapOf()
//
//    fun <T> getCustomData(name: String): T {
//        @Suppress("UNCHECKED_CAST")
//        return _customData[name] as T
//    }

    init {
        ServiceLoader.load(ValidatorService::class.java, MainProcessor::class.java.classLoader).forEach {
            it.validation(this)
        }
//        ServiceLoader.load(CustomDataService::class.java, MainProcessor::class.java.classLoader).forEach {
//            _customData += it.get(this)
//        }

    }

}