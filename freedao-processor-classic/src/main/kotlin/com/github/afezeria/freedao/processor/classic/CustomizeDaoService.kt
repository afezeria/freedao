package com.github.afezeria.freedao.processor.classic

import kotlin.reflect.KClass

/**
 *
 */
interface CustomizeDaoService {
    fun annotations(): List<KClass<*>>
}